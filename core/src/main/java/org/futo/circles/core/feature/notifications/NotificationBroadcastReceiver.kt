package org.futo.circles.core.feature.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.futo.circles.core.extensions.coroutineScope
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.read.ReadService
import javax.inject.Inject

@AndroidEntryPoint
class NotificationBroadcastReceiver: BroadcastReceiver() {

    @Inject
    lateinit var notificationDrawerManager: NotificationDrawerManager

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
