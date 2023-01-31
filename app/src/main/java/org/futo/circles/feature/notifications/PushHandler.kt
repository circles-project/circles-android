package org.futo.circles.feature.notifications

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.futo.circles.model.NotifiableMessageEvent
import org.futo.circles.model.PushData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent

class PushHandler(
    private val context: Context,
    private val notificationDrawerManager: NotificationDrawerManager,
    private val notifiableEventResolver: NotifiableEventResolver,
    private val wifiDetector: WifiDetector
) {

    private val coroutineScope = CoroutineScope(SupervisorJob())
    private val mUIHandler by lazy { Handler(Looper.getMainLooper()) }

    fun handle(pushData: PushData) {
        if (pushData.eventId == PushersManager.TEST_EVENT_ID) {
            LocalBroadcastManager.getInstance(context)
                .sendBroadcast(Intent(NotificationActionIds.push))
            return
        }

        mUIHandler.post {
            if (!ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                coroutineScope.launch(Dispatchers.IO) { handleInternal(pushData) }
        }
    }

    private suspend fun handleInternal(pushData: PushData) {
        try {
            val session = MatrixSessionProvider.currentSession ?: return
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

        if (wifiDetector.isConnectedToWifi().not()) return
        val event = tryOrNull { session.eventService().getEvent(pushData.roomId, pushData.eventId) }
            ?: return

        val resolvedEvent =
            notifiableEventResolver.resolveInMemoryEvent(session, event, canBeReplaced = true)

        if (resolvedEvent is NotifiableMessageEvent) {
            if (notificationDrawerManager.shouldIgnoreMessageEventInRoom(resolvedEvent)) return
        }

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