package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.model.InviteNotifiableEvent
import org.futo.circles.feature.notifications.model.NotifiableEvent
import org.futo.circles.feature.notifications.model.NotifiableMessageEvent
import org.futo.circles.feature.notifications.model.SimpleNotifiableEvent

data class NotificationEventQueue(
    private val queue: MutableList<NotifiableEvent>,
    private val seenEventIds: CircularCache<String>
) {

    fun markRedacted(eventIds: List<String>) {
        eventIds.forEach { redactedId ->
            queue.replace(redactedId) {
                when (it) {
                    is InviteNotifiableEvent -> it.copy(isRedacted = true)
                    is NotifiableMessageEvent -> it.copy(isRedacted = true)
                    is SimpleNotifiableEvent -> it.copy(isRedacted = true)
                }
            }
        }
    }

    fun syncRoomEvents(roomsLeft: Collection<String>, roomsJoined: Collection<String>) {
        if (roomsLeft.isNotEmpty() || roomsJoined.isNotEmpty()) {
            queue.removeAll {
                when (it) {
                    is NotifiableMessageEvent -> roomsLeft.contains(it.roomId)
                    is InviteNotifiableEvent -> roomsLeft.contains(it.roomId) || roomsJoined.contains(
                        it.roomId
                    )
                    else -> false
                }
            }
        }
    }

    fun isEmpty() = queue.isEmpty()

    fun clearAndAdd(events: List<NotifiableEvent>) {
        queue.clear()
        queue.addAll(events)
    }

    fun clear() {
        queue.clear()
    }

    fun add(notifiableEvent: NotifiableEvent) {
        val existing = findExistingById(notifiableEvent)
        val edited = findEdited(notifiableEvent)
        when {
            existing != null -> {
                if (existing.canBeReplaced) replace(replace = existing, with = notifiableEvent)
            }
            edited != null -> {
                replace(replace = edited, with = notifiableEvent)
            }
            seenEventIds.contains(notifiableEvent.eventId) -> {}
            else -> {
                seenEventIds.put(notifiableEvent.eventId)
                queue.add(notifiableEvent)
            }
        }
    }

    private fun findExistingById(notifiableEvent: NotifiableEvent): NotifiableEvent? {
        return queue.firstOrNull { it.eventId == notifiableEvent.eventId }
    }

    private fun findEdited(notifiableEvent: NotifiableEvent): NotifiableEvent? {
        return notifiableEvent.editedEventId?.let { editedId ->
            queue.firstOrNull {
                it.eventId == editedId || it.editedEventId == editedId
            }
        }
    }

    private fun replace(replace: NotifiableEvent, with: NotifiableEvent) {
        queue.remove(replace)
        queue.add(
            when (with) {
                is InviteNotifiableEvent -> with.copy(isUpdated = true)
                is NotifiableMessageEvent -> with.copy(isUpdated = true)
                is SimpleNotifiableEvent -> with.copy(isUpdated = true)
            }
        )
    }

    fun clearMemberShipNotificationForRoom(roomId: String) {
        queue.removeAll { it is InviteNotifiableEvent && it.roomId == roomId }
    }

    fun clearMessagesForRoom(roomId: String) {
        queue.removeAll { it is NotifiableMessageEvent && it.roomId == roomId }
    }

    fun clearMessagesForThread(roomId: String, threadId: String) {
        queue.removeAll { it is NotifiableMessageEvent && it.roomId == roomId && it.threadId == threadId }
    }

    fun rawEvents(): List<NotifiableEvent> = queue
}

private fun MutableList<NotifiableEvent>.replace(
    eventId: String,
    block: (NotifiableEvent) -> NotifiableEvent
) {
    val indexToReplace = indexOfFirst { it.eventId == eventId }
    if (indexToReplace == -1) return
    set(indexToReplace, block(get(indexToReplace)))
}