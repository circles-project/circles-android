package org.futo.circles.feature.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationManagerCompat
import org.futo.circles.feature.notifications.NotificationDrawerManager.Companion.ROOM_MESSAGES_NOTIFICATION_ID
import org.futo.circles.model.*

private typealias ProcessedMessageEvents = List<ProcessedEvent<NotifiableMessageEvent>>
class NotificationRenderer(
    context: Context,
    private val roomGroupMessageCreator: RoomGroupMessageCreator
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    @WorkerThread
    fun render(
        myUserDisplayName: String,
        myUserAvatarUrl: String?,
        eventsToProcess: List<ProcessedEvent<NotifiableMessageEvent>>
    ) {
        val roomEvents = eventsToProcess.groupByType()
        val roomNotifications = roomEvents.toNotifications(myUserDisplayName, myUserAvatarUrl)
        roomNotifications.forEach { wrapper ->
            when (wrapper) {
                is RoomNotification.Removed -> cancelNotificationMessage(wrapper.roomId)
                is RoomNotification.Message ->
                    showNotificationMessage(
                        wrapper.meta.roomId,
                        wrapper.notification
                    )
            }
        }
    }

    private fun List<ProcessedEvent<NotifiableMessageEvent>>.groupByType(): MutableMap<String, MutableList<ProcessedEvent<NotifiableMessageEvent>>> {
        val roomIdToEventMap: MutableMap<String, MutableList<ProcessedEvent<NotifiableMessageEvent>>> =
            LinkedHashMap()
        forEach {
            val roomEvents = roomIdToEventMap.getOrPut(it.event.roomId) { ArrayList() }
            roomEvents.add(it.castedToEventType())
        }
        return roomIdToEventMap
    }


    private fun Map<String, ProcessedMessageEvents>.toNotifications(
        myUserDisplayName: String,
        myUserAvatarUrl: String?
    ): List<RoomNotification> {
        return map { (roomId, events) ->
            when {
                events.hasNoEventsToDisplay() -> RoomNotification.Removed(roomId)
                else -> {
                    val messageEvents = events.onlyKeptEvents().filterNot { it.isRedacted }
                    roomGroupMessageCreator.createRoomMessage(
                        messageEvents,
                        roomId,
                        myUserDisplayName,
                        myUserAvatarUrl
                    )
                }
            }
        }
    }

    private fun ProcessedMessageEvents.hasNoEventsToDisplay() = isEmpty() || all {
        it.type == ProcessedEvent.Type.REMOVE || it.event.canNotBeDisplayed()
    }

    private fun NotifiableMessageEvent.canNotBeDisplayed() = isRedacted

    @Suppress("UNCHECKED_CAST")
    private fun <T : NotifiableMessageEvent> ProcessedEvent<NotifiableMessageEvent>.castedToEventType(): ProcessedEvent<T> =
        this as ProcessedEvent<T>


    @SuppressLint("MissingPermission")
    private fun showNotificationMessage(tag: String?, notification: Notification) {
        notificationManager.notify(tag, ROOM_MESSAGES_NOTIFICATION_ID, notification)
    }

    private fun cancelNotificationMessage(tag: String?) {
        notificationManager.cancel(tag, ROOM_MESSAGES_NOTIFICATION_ID)
    }

}
