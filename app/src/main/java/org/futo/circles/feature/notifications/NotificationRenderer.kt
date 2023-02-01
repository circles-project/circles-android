package org.futo.circles.feature.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationManagerCompat
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_EVENT_NOTIFICATION_ID
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_INVITATION_NOTIFICATION_ID
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_MESSAGES_NOTIFICATION_ID
import org.futo.circles.model.*

class NotificationRenderer(
    context: Context,
    private val notificationFactory: NotificationFactory
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    @WorkerThread
    fun render(
        myUserId: String,
        myUserDisplayName: String,
        myUserAvatarUrl: String?,
        eventsToProcess: List<ProcessedEvent<NotifiableEvent>>
    ) {
        val (roomEvents, simpleEvents, invitationEvents) = eventsToProcess.groupByType()
        with(notificationFactory) {
            val roomNotifications = roomEvents.toNotifications(myUserDisplayName, myUserAvatarUrl)
            val invitationNotifications = invitationEvents.toNotifications(myUserId)
            val simpleNotifications = simpleEvents.toNotifications(myUserId)

            roomNotifications.forEach { wrapper ->
                when (wrapper) {
                    is RoomNotification.Removed -> {
                        cancelNotificationMessage(
                            wrapper.roomId,
                            ROOM_MESSAGES_NOTIFICATION_ID
                        )
                    }
                    is RoomNotification.Message ->
                        showNotificationMessage(
                            wrapper.meta.roomId,
                            ROOM_MESSAGES_NOTIFICATION_ID,
                            wrapper.notification
                        )
                }
            }

            invitationNotifications.forEach { wrapper ->
                when (wrapper) {
                    is OneShotNotification.Removed -> {
                        cancelNotificationMessage(
                            wrapper.key,
                            ROOM_INVITATION_NOTIFICATION_ID
                        )
                    }
                    is OneShotNotification.Append ->
                        showNotificationMessage(
                            wrapper.meta.key,
                            ROOM_INVITATION_NOTIFICATION_ID,
                            wrapper.notification
                        )
                }
            }

            simpleNotifications.forEach { wrapper ->
                when (wrapper) {
                    is OneShotNotification.Removed -> {
                        cancelNotificationMessage(
                            wrapper.key,
                            ROOM_EVENT_NOTIFICATION_ID
                        )
                    }
                    is OneShotNotification.Append ->
                        showNotificationMessage(
                            wrapper.meta.key,
                            ROOM_EVENT_NOTIFICATION_ID,
                            wrapper.notification
                        )
                }
            }
        }
    }

    private fun List<ProcessedEvent<NotifiableEvent>>.groupByType(): GroupedNotificationEvents {
        val roomIdToEventMap: MutableMap<String, MutableList<ProcessedEvent<NotifiableMessageEvent>>> =
            LinkedHashMap()
        val simpleEvents: MutableList<ProcessedEvent<SimpleNotifiableEvent>> = ArrayList()
        val invitationEvents: MutableList<ProcessedEvent<InviteNotifiableEvent>> = ArrayList()
        forEach {
            when (val event = it.event) {
                is InviteNotifiableEvent -> invitationEvents.add(it.castedToEventType())
                is NotifiableMessageEvent -> {
                    val roomEvents = roomIdToEventMap.getOrPut(event.roomId) { ArrayList() }
                    roomEvents.add(it.castedToEventType())
                }
                is SimpleNotifiableEvent -> simpleEvents.add(it.castedToEventType())
            }
        }
        return GroupedNotificationEvents(roomIdToEventMap, simpleEvents, invitationEvents)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : NotifiableEvent> ProcessedEvent<NotifiableEvent>.castedToEventType(): ProcessedEvent<T> =
        this as ProcessedEvent<T>


    @SuppressLint("MissingPermission")
    private fun showNotificationMessage(tag: String?, id: Int, notification: Notification) {
        notificationManager.notify(tag, id, notification)
    }

    private fun cancelNotificationMessage(tag: String?, id: Int) {
        notificationManager.cancel(tag, id)
    }

}
