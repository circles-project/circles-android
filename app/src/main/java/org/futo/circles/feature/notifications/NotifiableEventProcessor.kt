package org.futo.circles.feature.notifications

import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.notifications.ProcessedEvent.Type.KEEP
import org.futo.circles.feature.notifications.ProcessedEvent.Type.REMOVE
import org.futo.circles.model.NotifiableEvent
import org.futo.circles.model.NotifiableMessageEvent
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