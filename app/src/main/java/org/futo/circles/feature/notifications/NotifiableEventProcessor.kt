package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.ProcessedEvent.Type.KEEP
import org.futo.circles.feature.notifications.ProcessedEvent.Type.REMOVE
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.getRoom

private typealias ProcessedEvents = List<ProcessedEvent<NotifiableEvent>>

class NotifiableEventProcessor {

    fun process(
        queuedEvents: List<NotifiableEvent>,
        currentRoomId: String?,
        currentThreadId: String?,
        renderedEvents: ProcessedEvents
    ): ProcessedEvents {
        val processedEvents = queuedEvents.map {
            val type = when (it) {
                is InviteNotifiableEvent -> KEEP
                is NotifiableMessageEvent -> when {
                    it.shouldIgnoreMessageEventInRoom(currentRoomId, currentThreadId) -> REMOVE
                    isMessageOutdated(it) -> REMOVE
                    else -> KEEP
                }
                is SimpleNotifiableEvent -> when (it.type) {
                    EventType.REDACTION -> REMOVE
                    else -> KEEP
                }
            }
            ProcessedEvent(type, it)
        }

        val removedEventsDiff = renderedEvents.filter { renderedEvent ->
            queuedEvents.none { it.eventId == renderedEvent.event.eventId }
        }.map { ProcessedEvent(REMOVE, it.event) }

        return removedEventsDiff + processedEvents
    }

    private fun isMessageOutdated(notifiableEvent: NotifiableEvent): Boolean {
        val session = MatrixSessionProvider.currentSession ?: return false

        if (notifiableEvent is NotifiableMessageEvent) {
            val eventID = notifiableEvent.eventId
            val roomID = notifiableEvent.roomId
            val room = session.getRoom(roomID) ?: return false
            return room.readService().isEventRead(eventID)
        }
        return false
    }
}