package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.ProcessedEvent.Type.KEEP
import org.futo.circles.feature.notifications.ProcessedEvent.Type.REMOVE
import org.futo.circles.model.NotifiableMessageEvent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom

private typealias ProcessedEvents = List<ProcessedEvent<NotifiableMessageEvent>>

class NotifiableEventProcessor {

    fun process(
        queuedEvents: List<NotifiableMessageEvent>,
        renderedEvents: ProcessedEvents
    ): ProcessedEvents {
        val processedEvents = queuedEvents.map {
            val type = when {
                isMessageOutdated(it) -> REMOVE
                else -> KEEP
            }
            ProcessedEvent(type, it)
        }

        val removedEventsDiff = renderedEvents.filter { renderedEvent ->
            queuedEvents.none { it.eventId == renderedEvent.event.eventId }
        }.map { ProcessedEvent(REMOVE, it.event) }

        return removedEventsDiff + processedEvents
    }

    private fun isMessageOutdated(NotifiableMessageEvent: NotifiableMessageEvent): Boolean {
        val session = MatrixSessionProvider.currentSession ?: return false
        val eventID = NotifiableMessageEvent.eventId
        val roomID = NotifiableMessageEvent.roomId
        val room = session.getRoom(roomID) ?: return false
        return room.readService().isEventRead(eventID)
    }
}