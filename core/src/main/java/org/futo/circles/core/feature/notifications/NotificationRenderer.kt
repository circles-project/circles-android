package org.futo.circles.core.feature.notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.feature.notifications.NotificationDrawerManager.Companion.ROOM_INVITATION_NOTIFICATION_ID
import org.futo.circles.core.feature.notifications.NotificationDrawerManager.Companion.ROOM_MESSAGES_NOTIFICATION_ID
import org.futo.circles.core.model.GroupedNotificationEvents
import org.futo.circles.core.model.InviteNotifiableEvent
import org.futo.circles.core.model.NotifiableEvent
import org.futo.circles.core.model.NotifiableMessageEvent
import org.futo.circles.core.model.OneShotNotification
import org.futo.circles.core.model.RoomNotification
import javax.inject.Inject

private typealias ProcessedMessageEvents = List<ProcessedEvent<NotifiableMessageEvent>>

class NotificationRenderer @Inject constructor(
    @ApplicationContext context: Context,
    private val roomGroupMessageCreator: RoomGroupMessageCreator,
    private val notificationUtils: NotificationUtils
) {

    private val notificationManager = NotificationManagerCompat.from(context)

    @WorkerThread
    fun render(
        myUserDisplayName: String,
        myUserAvatarUrl: String?,
        eventsToProcess: List<ProcessedEvent<NotifiableEvent>>
    ) {
        val (roomEvents, invitationEvents) = eventsToProcess.groupByType()
        val roomNotifications = roomEvents.toNotifications(myUserDisplayName, myUserAvatarUrl)
        val invitationNotifications = invitationEvents.toNotifications()

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

    @JvmName("toNotificationsInviteNotifiableEvent")
    fun List<ProcessedEvent<InviteNotifiableEvent>>.toNotifications(): List<OneShotNotification> {
        return map { (processed, event) ->
            when (processed) {
                ProcessedEvent.Type.REMOVE -> OneShotNotification.Removed(key = event.roomId)
                ProcessedEvent.Type.KEEP -> OneShotNotification.Append(
                    notificationUtils.buildRoomInvitationNotification(event),
                    OneShotNotification.Append.Meta(
                        key = event.roomId,
                        summaryLine = event.description,
                        isNoisy = event.noisy,
                        timestamp = event.timestamp
                    )
                )
            }
        }
    }


    private fun ProcessedMessageEvents.hasNoEventsToDisplay() = isEmpty() || all {
        it.type == ProcessedEvent.Type.REMOVE || it.event.isRedacted
    }

    @SuppressLint("MissingPermission")
    private fun showNotificationMessage(tag: String?, id: Int, notification: Notification) {
        notificationManager.notify(tag, id, notification)
    }

    private fun cancelNotificationMessage(tag: String?, id: Int) {
        notificationManager.cancel(tag, id)
    }

    private fun List<ProcessedEvent<NotifiableEvent>>.groupByType(): GroupedNotificationEvents {
        val roomIdToEventMap: MutableMap<String, MutableList<ProcessedEvent<NotifiableMessageEvent>>> =
            LinkedHashMap()
        val invitationEvents: MutableList<ProcessedEvent<InviteNotifiableEvent>> = ArrayList()
        forEach {
            when (val event = it.event) {
                is InviteNotifiableEvent -> invitationEvents.add(it.castedToEventType())
                is NotifiableMessageEvent -> {
                    val roomEvents = roomIdToEventMap.getOrPut(event.roomId) { ArrayList() }
                    roomEvents.add(it.castedToEventType())
                }
            }
        }
        return GroupedNotificationEvents(roomIdToEventMap, invitationEvents)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : NotifiableEvent> ProcessedEvent<NotifiableEvent>.castedToEventType(): ProcessedEvent<T> =
        this as ProcessedEvent<T>

}
