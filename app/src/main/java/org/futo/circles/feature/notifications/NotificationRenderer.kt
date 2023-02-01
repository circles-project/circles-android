package org.futo.circles.feature.notifications

import androidx.annotation.WorkerThread
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_EVENT_NOTIFICATION_ID
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_INVITATION_NOTIFICATION_ID
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_MESSAGES_NOTIFICATION_ID
import org.futo.circles.model.*

class NotificationRenderer(
    private val notificationDisplayer: NotificationDisplayer,
    private val notificationFactory: NotificationFactory
) {

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
                        notificationDisplayer.cancelNotificationMessage(
                            wrapper.roomId,
                            ROOM_MESSAGES_NOTIFICATION_ID
                        )
                    }
                    is RoomNotification.Message ->
                        notificationDisplayer.showNotificationMessage(
                            wrapper.meta.roomId,
                            ROOM_MESSAGES_NOTIFICATION_ID,
                            wrapper.notification
                        )
                }
            }

            invitationNotifications.forEach { wrapper ->
                when (wrapper) {
                    is OneShotNotification.Removed -> {
                        notificationDisplayer.cancelNotificationMessage(
                            wrapper.key,
                            ROOM_INVITATION_NOTIFICATION_ID
                        )
                    }
                    is OneShotNotification.Append ->
                        notificationDisplayer.showNotificationMessage(
                            wrapper.meta.key,
                            ROOM_INVITATION_NOTIFICATION_ID,
                            wrapper.notification
                        )
                }
            }

            simpleNotifications.forEach { wrapper ->
                when (wrapper) {
                    is OneShotNotification.Removed -> {
                        notificationDisplayer.cancelNotificationMessage(
                            wrapper.key,
                            ROOM_EVENT_NOTIFICATION_ID
                        )
                    }
                    is OneShotNotification.Append ->
                        notificationDisplayer.showNotificationMessage(
                            wrapper.meta.key,
                            ROOM_EVENT_NOTIFICATION_ID,
                            wrapper.notification
                        )
                }
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

data class GroupedNotificationEvents(
    val roomEvents: Map<String, List<ProcessedEvent<NotifiableMessageEvent>>>,
    val simpleEvents: List<ProcessedEvent<SimpleNotifiableEvent>>,
    val invitationEvents: List<ProcessedEvent<InviteNotifiableEvent>>
)
