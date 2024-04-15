package org.futo.circles.core.feature.notifications

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.futo.circles.core.model.NotifiableEvent
import org.futo.circles.core.model.toNotificationAction
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.pushrules.PushEvents
import org.matrix.android.sdk.api.session.pushrules.PushRuleService
import org.matrix.android.sdk.api.session.pushrules.getActions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushRuleTriggerListener @Inject constructor(
    private val resolver: NotifiableEventResolver,
    private val notificationDrawerManager: NotificationDrawerManager
) : PushRuleService.PushRuleListener {

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())

    override fun onEvents(pushEvents: PushEvents) {
        scope.launch {
            MatrixSessionProvider.currentSession?.let { session ->
                val notifiableEvents = createNotifiableEvents(pushEvents, session)
                notificationDrawerManager.updateEvents { queuedEvents ->
                    notifiableEvents.forEach { notifiableEvent ->
                        queuedEvents.onNotifiableEventReceived(notifiableEvent)
                    }
                    queuedEvents.syncRoomEvents(
                        roomsLeft = pushEvents.roomsLeft,
                        roomsJoined = pushEvents.roomsJoined
                    )
                    queuedEvents.markRedacted(pushEvents.redactedEventIds)
                }
            }
        }
    }

    private suspend fun createNotifiableEvents(
        pushEvents: PushEvents,
        session: Session
    ): List<NotifiableEvent> {
        return pushEvents.matchedEvents.mapNotNull { (event, pushRule) ->
            val action = pushRule.getActions().toNotificationAction()
            if (action.shouldNotify) {
                resolver.resolveEvent(event, session)
            } else null
        }
    }

    fun startWithSession(session: Session) {
        session.pushRuleService().addPushRuleListener(this)
    }

    fun stop() {
        scope.coroutineContext.cancelChildren(CancellationException("PushRuleTriggerListener stopping"))
        MatrixSessionProvider.currentSession?.pushRuleService()?.removePushRuleListener(this)
        notificationDrawerManager.clearAllEvents()
    }
}
