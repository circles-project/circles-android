package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.model.*

private typealias ProcessedMessageEvents = List<ProcessedEvent<NotifiableMessageEvent>>

class NotificationFactory(
    private val notificationUtils: NotificationUtils,
    private val roomGroupMessageCreator: RoomGroupMessageCreator,
    private val summaryGroupMessageCreator: SummaryGroupMessageCreator
) {

    fun Map<String, ProcessedMessageEvents>.toNotifications(
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

    @JvmName("toNotificationsInviteNotifiableEvent")
    fun List<ProcessedEvent<InviteNotifiableEvent>>.toNotifications(myUserId: String): List<OneShotNotification> {
        return map { (processed, event) ->
            when (processed) {
                ProcessedEvent.Type.REMOVE -> OneShotNotification.Removed(key = event.roomId)
                ProcessedEvent.Type.KEEP -> OneShotNotification.Append(
                    notificationUtils.buildRoomInvitationNotification(event, myUserId),
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

    @JvmName("toNotificationsSimpleNotifiableEvent")
    fun List<ProcessedEvent<SimpleNotifiableEvent>>.toNotifications(myUserId: String): List<OneShotNotification> {
        return map { (processed, event) ->
            when (processed) {
                ProcessedEvent.Type.REMOVE -> OneShotNotification.Removed(key = event.eventId)
                ProcessedEvent.Type.KEEP -> OneShotNotification.Append(
                    notificationUtils.buildSimpleEventNotification(event, myUserId),
                    OneShotNotification.Append.Meta(
                        key = event.eventId,
                        summaryLine = event.description,
                        isNoisy = event.noisy,
                        timestamp = event.timestamp
                    )
                )
            }
        }
    }

    fun createSummaryNotification(
        roomNotifications: List<RoomNotification>,
        invitationNotifications: List<OneShotNotification>,
        simpleNotifications: List<OneShotNotification>,
        useCompleteNotificationFormat: Boolean
    ): SummaryNotification {
        val roomMeta =
            roomNotifications.filterIsInstance<RoomNotification.Message>().map { it.meta }
        val invitationMeta =
            invitationNotifications.filterIsInstance<OneShotNotification.Append>().map { it.meta }
        val simpleMeta =
            simpleNotifications.filterIsInstance<OneShotNotification.Append>().map { it.meta }
        return when {
            roomMeta.isEmpty() && invitationMeta.isEmpty() && simpleMeta.isEmpty() -> SummaryNotification.Removed
            else -> SummaryNotification.Update(
                summaryGroupMessageCreator.createSummaryNotification(
                    roomNotifications = roomMeta,
                    invitationNotifications = invitationMeta,
                    simpleNotifications = simpleMeta,
                    useCompleteNotificationFormat = useCompleteNotificationFormat
                )
            )
        }
    }
}
