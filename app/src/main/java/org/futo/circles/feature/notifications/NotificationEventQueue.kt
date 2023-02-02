package org.futo.circles.feature.notifications

import org.futo.circles.model.NotifiableMessageEvent

data class NotificationEventQueue(
    private val queue: MutableList<NotifiableMessageEvent>,
    private val seenEventIds: CircularCache<String>
) {

    fun markRedacted(eventIds: List<String>) {
        eventIds.forEach { redactedId ->
            queue.replace(redactedId) { it.copy(isRedacted = true) }
        }
    }

    fun syncRoomEvents(roomsLeft: Collection<String>, roomsJoined: Collection<String>) {
        if (roomsLeft.isNotEmpty() || roomsJoined.isNotEmpty()) {
            queue.removeAll { roomsLeft.contains(it.roomId) }
        }
    }

    fun isEmpty() = queue.isEmpty()

    fun clearAndAdd(events: List<NotifiableMessageEvent>) {
        queue.clear()
        queue.addAll(events)
    }

    fun clear() {
        queue.clear()
    }

    fun add(notifiableEvent: NotifiableMessageEvent) {
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

    private fun findExistingById(notifiableEvent: NotifiableMessageEvent): NotifiableMessageEvent? {
        return queue.firstOrNull { it.eventId == notifiableEvent.eventId }
    }

    private fun findEdited(notifiableEvent: NotifiableMessageEvent): NotifiableMessageEvent? {
        return notifiableEvent.editedEventId?.let { editedId ->
            queue.firstOrNull {
                it.eventId == editedId || it.editedEventId == editedId
            }
        }
    }

    private fun replace(replace: NotifiableMessageEvent, with: NotifiableMessageEvent) {
        queue.remove(replace)
        queue.add(with.copy(isUpdated = true))
    }

    fun clearMessagesForRoom(roomId: String) {
        queue.removeAll { it.roomId == roomId }
    }

    fun rawEvents(): List<NotifiableMessageEvent> = queue
}

private fun MutableList<NotifiableMessageEvent>.replace(
    eventId: String,
    block: (NotifiableMessageEvent) -> NotifiableMessageEvent
) {
    val indexToReplace = indexOfFirst { it.eventId == eventId }
    if (indexToReplace == -1) return
    set(indexToReplace, block(get(indexToReplace)))
}