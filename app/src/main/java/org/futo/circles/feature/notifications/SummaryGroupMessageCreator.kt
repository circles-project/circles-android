package org.futo.circles.feature.notifications

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import org.futo.circles.R
import org.futo.circles.model.OneShotNotification
import org.futo.circles.model.RoomNotification


class SummaryGroupMessageCreator(
    private val context: Context,
    private val notificationUtils: NotificationUtils
) {

    fun createSummaryNotification(
        roomNotifications: List<RoomNotification.Message.Meta>,
        invitationNotifications: List<OneShotNotification.Append.Meta>,
        simpleNotifications: List<OneShotNotification.Append.Meta>,
        useCompleteNotificationFormat: Boolean
    ): Notification {
        val summaryInboxStyle = NotificationCompat.InboxStyle().also { style ->
            roomNotifications.forEach { style.addLine(it.summaryLine) }
            invitationNotifications.forEach { style.addLine(it.summaryLine) }
            simpleNotifications.forEach { style.addLine(it.summaryLine) }
        }

        val summaryIsNoisy = roomNotifications.any { it.shouldBing } ||
                invitationNotifications.any { it.isNoisy } ||
                simpleNotifications.any { it.isNoisy }

        val messageCount =
            roomNotifications.fold(initial = 0) { acc, current -> acc + current.messageCount }

        val lastMessageTimestamp = roomNotifications.lastOrNull()?.latestTimestamp
            ?: invitationNotifications.lastOrNull()?.timestamp
            ?: simpleNotifications.last().timestamp

        // FIXME roomIdToEventMap.size is not correct, this is the number of rooms
        val nbEvents = roomNotifications.size + simpleNotifications.size
        val sumTitle = context.resources.getQuantityString(
            R.plurals.notification_compat_summary_title,
            nbEvents,
            nbEvents
        )
        summaryInboxStyle.setBigContentTitle(sumTitle)
            // TODO get latest event?
            .setSummaryText(
                context.resources.getQuantityString(
                    R.plurals.notification_unread_notified_messages,
                    nbEvents,
                    nbEvents
                )
            )
        return if (useCompleteNotificationFormat) {
            notificationUtils.buildSummaryListNotification(
                summaryInboxStyle,
                sumTitle,
                noisy = summaryIsNoisy,
                lastMessageTimestamp = lastMessageTimestamp
            )
        } else {
            processSimpleGroupSummary(
                summaryIsNoisy,
                messageCount,
                simpleNotifications.size,
                invitationNotifications.size,
                roomNotifications.size,
                lastMessageTimestamp
            )
        }
    }

    private fun processSimpleGroupSummary(
        summaryIsNoisy: Boolean,
        messageEventsCount: Int,
        simpleEventsCount: Int,
        invitationEventsCount: Int,
        roomCount: Int,
        lastMessageTimestamp: Long
    ): Notification {
        // Add the simple events as message (?)
        val messageNotificationCount = messageEventsCount + simpleEventsCount

        val privacyTitle = if (invitationEventsCount > 0) {
            val invitationsStr = context.resources.getQuantityString(
                R.plurals.notification_invitations,
                invitationEventsCount,
                invitationEventsCount
            )
            if (messageNotificationCount > 0) {
                // Invitation and message
                val messageStr = context.resources.getQuantityString(
                    R.plurals.room_new_messages_notification,
                    messageNotificationCount, messageNotificationCount
                )
                if (roomCount > 1) {
                    // In several rooms
                    val roomStr = context.resources.getQuantityString(
                        R.plurals.notification_unread_notified_messages_in_room_rooms,
                        roomCount, roomCount
                    )
                    context.getString(
                        R.string.notification_unread_notified_messages_in_room_and_invitation,
                        messageStr,
                        roomStr,
                        invitationsStr
                    )
                } else {
                    // In one room
                    context.getString(
                        R.string.notification_unread_notified_messages_and_invitation,
                        messageStr,
                        invitationsStr
                    )
                }
            } else {
                // Only invitation
                invitationsStr
            }
        } else {
            // No invitation, only messages
            val messageStr = context.resources.getQuantityString(
                R.plurals.room_new_messages_notification,
                messageNotificationCount, messageNotificationCount
            )
            if (roomCount > 1) {
                // In several rooms
                val roomStr = context.resources.getQuantityString(
                    R.plurals.notification_unread_notified_messages_in_room_rooms,
                    roomCount,
                    roomCount
                )
                context.getString(
                    R.string.notification_unread_notified_messages_in_room,
                    messageStr,
                    roomStr
                )
            } else {
                // In one room
                messageStr
            }
        }
        return notificationUtils.buildSummaryListNotification(
            style = null,
            compatSummary = privacyTitle,
            noisy = summaryIsNoisy,
            lastMessageTimestamp = lastMessageTimestamp
        )
    }
}
