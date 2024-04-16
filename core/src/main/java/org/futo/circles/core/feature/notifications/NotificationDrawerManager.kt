package org.futo.circles.core.feature.notifications

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.R
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.NotifiableEvent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.getUserOrDefault
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDrawerManager @Inject constructor(
    @ApplicationContext context: Context,
    private val notifiableEventProcessor: NotifiableEventProcessor,
    private val notificationRenderer: NotificationRenderer,
    private val notificationEventPersistence: NotificationEventPersistence
) {

    private val handlerThread: HandlerThread =
        HandlerThread("NotificationDrawerManager", Thread.MIN_PRIORITY)
    private var backgroundHandler: Handler

    private val currentSession: Session?
        get() = MatrixSessionProvider.currentSession

    private val notificationState by lazy { createInitialNotificationState() }
    private val avatarSize = context.resources.getDimensionPixelSize(R.dimen.profile_avatar_size)
    private val firstThrottler = FirstThrottler(200)

    init {
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
    }

    private fun createInitialNotificationState(): NotificationState {
        val queuedEvents = notificationEventPersistence.loadEvents(factory = { rawEvents ->
            NotificationEventQueue(
                rawEvents.toMutableList(),
                seenEventIds = CircularCache.create(cacheSize = 25)
            )
        })
        val renderedEvents =
            queuedEvents.rawEvents().map { ProcessedEvent(ProcessedEvent.Type.KEEP, it) }
                .toMutableList()
        return NotificationState(queuedEvents, renderedEvents)
    }

    fun NotificationEventQueue.onNotifiableEventReceived(notifiableEvent: NotifiableEvent) {
        add(notifiableEvent)
    }

    fun updateEvents(action: NotificationDrawerManager.(NotificationEventQueue) -> Unit) {
        notificationState.updateQueuedEvents(this) { queuedEvents, _ ->
            action(queuedEvents)
        }
        refreshNotificationDrawer()
    }

    fun clearAllEvents() {
        updateEvents { it.clear() }
    }

    private fun refreshNotificationDrawer() {
        val canHandle = firstThrottler.canHandle()
        backgroundHandler.removeCallbacksAndMessages(null)
        backgroundHandler.postDelayed(
            { tryOrNull { refreshNotificationDrawerBg() } },
            canHandle.waitMillis()
        )
    }

    @WorkerThread
    private fun refreshNotificationDrawerBg() {
        val eventsToRender =
            notificationState.updateQueuedEvents(this) { queuedEvents, renderedEvents ->
                notifiableEventProcessor.process(
                    queuedEvents.rawEvents(),
                    renderedEvents
                ).also {
                    queuedEvents.clearAndAdd(it.onlyKeptEvents())
                }
            }

        if (!notificationState.hasAlreadyRendered(eventsToRender)) {
            notificationState.clearAndAddRenderedEvents(eventsToRender)
            val session = currentSession ?: return
            renderEvents(session, eventsToRender)
            persistEvents()
        }
    }

    private fun persistEvents() {
        notificationState.queuedEvents { queuedEvents ->
            notificationEventPersistence.persistEvents(queuedEvents)
        }
    }

    private fun renderEvents(
        session: Session,
        eventsToRender: List<ProcessedEvent<NotifiableEvent>>
    ) {
        val user = session.getUserOrDefault(session.myUserId)
        val myUserDisplayName = user.notEmptyDisplayName()
        val myUserAvatarUrl = session.contentUrlResolver().resolveThumbnail(
            contentUrl = user.avatarUrl,
            width = avatarSize,
            height = avatarSize,
            method = ContentUrlResolver.ThumbnailMethod.SCALE
        )
        notificationRenderer.render(myUserDisplayName, myUserAvatarUrl, eventsToRender)
    }

    companion object {
        const val ROOM_MESSAGES_NOTIFICATION_ID = 1
        const val ROOM_INVITATION_NOTIFICATION_ID = 2
    }
}
