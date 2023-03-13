package org.futo.circles.feature.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.launch
import org.futo.circles.extensions.coroutineScope
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.read.ReadService


class NotificationBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationDrawerManager: NotificationDrawerManager by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        when (intent.action) {
            NotificationActionIds.dismissRoom ->
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMessagesForRoom(roomId) }
                }
            NotificationActionIds.markRoomRead ->
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMessagesForRoom(roomId) }
                    handleMarkAsRead(roomId)
                }
        }
    }

    private fun handleMarkAsRead(roomId: String) {
        MatrixSessionProvider.currentSession?.let { session ->
            val room = session.getRoom(roomId)
            if (room != null) {
                session.coroutineScope.launch {
                    tryOrNull {
                        room.readService().markAsRead(
                            ReadService.MarkAsReadParams.READ_RECEIPT,
                            mainTimeLineOnly = false
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_ROOM_ID = "roomID"
    }
}
