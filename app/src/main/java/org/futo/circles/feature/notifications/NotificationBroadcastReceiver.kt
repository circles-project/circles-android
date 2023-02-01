package org.futo.circles.feature.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import kotlinx.coroutines.launch
import org.futo.circles.R
import org.futo.circles.extensions.coroutineScope
import org.futo.circles.model.NotifiableMessageEvent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.read.ReadService
import java.util.*


class NotificationBroadcastReceiver(
    private val notificationDrawerManager: NotificationDrawerManager
) : BroadcastReceiver() {


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
