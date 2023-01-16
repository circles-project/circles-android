package org.futo.circles.feature.notifications

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.*
import org.futo.circles.feature.notifications.model.PushData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent

class PushHandler(
    private val notificationDrawerManager: NotificationDrawerManager,
    private val notifiableEventResolver: NotifiableEventResolver,
    private val activeSessionHolder: ActiveSessionHolder,
    private val vectorPreferences: VectorPreferences,
    private val vectorDataStore: VectorDataStore,
    private val wifiDetector: WifiDetector,
    private val actionIds: NotificationActionIds,
    private val context: Context
) {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    private val mUIHandler by lazy { Handler(Looper.getMainLooper()) }

    fun handle(pushData: PushData) {
        runBlocking { vectorDataStore.incrementPushCounter() }

        if (!vectorPreferences.areNotificationEnabledForDevice()) return

        mUIHandler.post {
            if (!ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                coroutineScope.launch(Dispatchers.IO) { handleInternal(pushData) }
        }
    }

    private suspend fun handleInternal(pushData: PushData) {
        try {
            val session = activeSessionHolder.getOrInitializeSession(startSync = false) ?: return

            if (!isEventAlreadyKnown(pushData)) {
                getEventFastLane(session, pushData)
                session.syncService().requireBackgroundSync()
            }
        } catch (ignore: Exception) {
        }
    }

    private suspend fun getEventFastLane(session: Session, pushData: PushData) {
        pushData.roomId ?: return
        pushData.eventId ?: return

        if (notificationDrawerManager.shouldIgnoreMessageEventInRoom(pushData.roomId)) return
        if (wifiDetector.isConnectedToWifi().not()) return

        val event = tryOrNull { session.eventService().getEvent(pushData.roomId, pushData.eventId) }
            ?: return

        val resolvedEvent =
            notifiableEventResolver.resolveInMemoryEvent(session, event, canBeReplaced = true)

        resolvedEvent?.let {
            notificationDrawerManager.updateEvents { it.onNotifiableEventReceived(resolvedEvent) }
        }
    }

    private fun isEventAlreadyKnown(pushData: PushData): Boolean {
        if (pushData.eventId != null && pushData.roomId != null) {
            try {
                val session = MatrixSessionProvider.currentSession ?: return false
                val room = session.getRoom(pushData.roomId) ?: return false
                return room.getTimelineEvent(pushData.eventId) != null
            } catch (ignore: Exception) {
            }
        }
        return false
    }
}