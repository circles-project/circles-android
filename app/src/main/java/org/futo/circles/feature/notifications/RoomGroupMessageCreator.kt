package org.futo.circles.feature.notifications

import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.model.NotifiableMessageEvent
import org.futo.circles.model.RoomEventGroupInfo
import org.futo.circles.model.RoomNotification
import javax.inject.Inject


class RoomGroupMessageCreator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bitmapLoader: NotificationBitmapLoader,
    private val notificationUtils: NotificationUtils
) {

    fun createRoomMessage(
        events: List<NotifiableMessageEvent>,
        roomId: String,
        userDisplayName: String,
        userAvatarUrl: String?
    ): RoomNotification.Message {
        val lastKnownRoomEvent = events.last()
        val roomName = lastKnownRoomEvent.roomName ?: lastKnownRoomEvent.senderName ?: ""
        val style = NotificationCompat.MessagingStyle(
            Person.Builder()
                .setName(userDisplayName)
                .setIcon(bitmapLoader.getUserIcon(userAvatarUrl))
                .setKey(lastKnownRoomEvent.matrixID)
                .build()
        ).also {
            it.conversationTitle = roomName
            it.isGroupConversation = true
            it.addMessagesFromEvents(events)
        }

        val tickerText = context.getString(
            R.string.notification_ticker_text_group,
            roomName,
            events.last().senderName,
            events.last().description
        )
        val largeBitmap = getRoomBitmap(events)

        val lastMessageTimestamp = events.last().timestamp
        val messageCount = events.size
        val meta = RoomNotification.Message.Meta(
            summaryLine = createRoomMessagesGroupSummaryLine(
                events,
                roomName
            ),
            messageCount = messageCount,
            latestTimestamp = lastMessageTimestamp,
            roomId = roomId,
            shouldBing = events.any { it.noisy }
        )
        return RoomNotification.Message(
            notificationUtils.buildMessagesListNotification(
                style,
                RoomEventGroupInfo(roomId, roomName).also {
                    it.isUpdated = events.last().isUpdated
                },
                largeIcon = largeBitmap,
                lastMessageTimestamp,
                tickerText
            ),
            meta
        )
    }

    private fun NotificationCompat.MessagingStyle.addMessagesFromEvents(events: List<NotifiableMessageEvent>) {
        events.forEach { event ->
            val senderPerson = if (event.outGoingMessage) {
                null
            } else {
                Person.Builder()
                    .setName(event.senderName)
                    .setIcon(bitmapLoader.getUserIcon(event.senderAvatarPath))
                    .setKey(event.senderId)
                    .build()
            }
            val message = NotificationCompat.MessagingStyle.Message(
                event.body,
                event.timestamp,
                senderPerson
            ).also { message ->
                event.imageUri?.let {
                    message.setData("image/", it)
                }
            }
            addMessage(message)
        }
    }

    private fun createRoomMessagesGroupSummaryLine(
        events: List<NotifiableMessageEvent>,
        roomName: String
    ): CharSequence {
        return try {
            when (events.size) {
                1 -> String.format(
                    "%s: %s ",
                    roomName,
                    events.first().senderName
                ) + events.first().description
                else -> {
                    context.resources.getQuantityString(
                        R.plurals.notification_compat_summary_line_for_room,
                        events.size,
                        roomName,
                        events.size
                    )
                }
            }
        } catch (e: Throwable) {
            roomName
        }
    }

    private fun getRoomBitmap(events: List<NotifiableMessageEvent>): Bitmap? {
        return events.lastOrNull()
            ?.let { bitmapLoader.getRoomBitmap(it.roomName ?: "", it.roomAvatarPath) }
    }
}