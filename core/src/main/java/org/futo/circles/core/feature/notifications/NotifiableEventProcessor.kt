package org.futo.circles.core.feature.notifications

import org.futo.circles.core.model.NotifiableEvent
import org.futo.circles.core.model.NotifiableMessageEvent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

private typealias ProcessedEvents = List<ProcessedEvent<NotifiableEvent>>

class NotifiableEventProcessor @Inject constructor() {

    fun process(
        queuedEvents: List<NotifiableEvent>,
        renderedEvents: ProcessedEvents
    ): ProcessedEvents {
        val processedEvents = queuedEvents.map {
            val type = when {
                isMessageOutdated(it) -> ProcessedEvent.Type.REMOVE
                else -> ProcessedEvent.Type.KEEP
            }
            ProcessedEvent(type, it)
        }

        val removedEventsDiff = renderedEvents.filter { renderedEvent ->
            queuedEvents.none { it.eventId == renderedEvent.event.eventId }
        }.map { ProcessedEvent(ProcessedEvent.Type.REMOVE, it.event) }

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