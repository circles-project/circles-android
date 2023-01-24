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
    private val notificationDrawerManager: NotificationDrawerManager,
    private val actionIds: NotificationActionIds
) : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) return
        when (intent.action) {
            actionIds.smartReply ->
                handleSmartReply(intent, context)
            actionIds.dismissRoom ->
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMessagesForRoom(roomId) }
                }
            actionIds.dismissSummary ->
                notificationDrawerManager.clearAllEvents()
            actionIds.markRoomRead ->
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents { it.clearMessagesForRoom(roomId) }
                    handleMarkAsRead(roomId)
                }
            actionIds.join -> {
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents {
                        it.clearMemberShipNotificationForRoom(
                            roomId
                        )
                    }
                    handleJoinRoom(roomId)
                }
            }
            actionIds.reject -> {
                intent.getStringExtra(KEY_ROOM_ID)?.let { roomId ->
                    notificationDrawerManager.updateEvents {
                        it.clearMemberShipNotificationForRoom(
                            roomId
                        )
                    }
                    handleRejectRoom(roomId)
                }
            }
        }
    }

    private fun handleJoinRoom(roomId: String) {
        MatrixSessionProvider.currentSession?.let { session ->
            val room = session.getRoom(roomId)
            if (room != null) {
                session.coroutineScope.launch {
                    tryOrNull {
                        session.roomService().joinRoom(room.roomId)
                    }
                }
            }
        }
    }

    private fun handleRejectRoom(roomId: String) {
        MatrixSessionProvider.currentSession?.let { session ->
            session.coroutineScope.launch {
                tryOrNull { session.roomService().leaveRoom(roomId) }
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

    private fun handleSmartReply(intent: Intent, context: Context) {
        val message = getReplyMessage(intent)
        val roomId = intent.getStringExtra(KEY_ROOM_ID)
        val threadId = intent.getStringExtra(KEY_THREAD_ID)

        if (message.isNullOrBlank() || roomId.isNullOrBlank()) {
            // ignore this event
            // Can this happen? should we update notification?
            return
        }
        MatrixSessionProvider.currentSession?.let { session ->
            session.getRoom(roomId)?.let { room ->
                sendMatrixEvent(message, threadId, session, room, context)
            }
        }
    }

    private fun sendMatrixEvent(
        message: String,
        threadId: String?,
        session: Session,
        room: Room,
        context: Context?
    ) {
        if (threadId != null) {
            room.relationService().replyInThread(
                rootThreadEventId = threadId,
                replyInThreadText = message,
            )
        } else {
            room.sendService().sendTextMessage(message)
        }

        // Create a new event to be displayed in the notification drawer, right now

        val notifiableMessageEvent = NotifiableMessageEvent(
            // Generate a Fake event id
            eventId = UUID.randomUUID().toString(),
            editedEventId = null,
            noisy = false,
            timestamp = System.currentTimeMillis(),
            senderName = session.roomService()
                .getRoomMember(session.myUserId, room.roomId)?.displayName
                ?: context?.getString(R.string.notification_sender_me),
            senderId = session.myUserId,
            body = message,
            imageUriString = null,
            roomId = room.roomId,
            threadId = threadId,
            roomName = room.roomSummary()?.displayName ?: room.roomId,
            roomIsDirect = room.roomSummary()?.isDirect == true,
            outGoingMessage = true,
            canBeReplaced = false
        )

        notificationDrawerManager.updateEvents { it.onNotifiableEventReceived(notifiableMessageEvent) }
    }

    private fun getReplyMessage(intent: Intent?): String? {
        if (intent != null) {
            val remoteInput = RemoteInput.getResultsFromIntent(intent)
            if (remoteInput != null) {
                return remoteInput.getCharSequence(KEY_TEXT_REPLY)?.toString()
            }
        }
        return null
    }

    companion object {
        const val KEY_ROOM_ID = "roomID"
        const val KEY_THREAD_ID = "threadID"
        const val KEY_TEXT_REPLY = "key_text_reply"
    }
}
