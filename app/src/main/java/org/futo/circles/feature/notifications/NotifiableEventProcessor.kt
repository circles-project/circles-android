package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.ProcessedEvent.Type.KEEP
import org.futo.circles.feature.notifications.ProcessedEvent.Type.REMOVE
import org.futo.circles.feature.notifications.model.*
import org.matrix.android.sdk.api.session.events.model.EventType

private typealias ProcessedEvents = List<ProcessedEvent<NotifiableEvent>>

class NotifiableEventProcessor (
        private val outdatedDetector: OutdatedEventDetector,
        private val autoAcceptInvites: AutoAcceptInvites
) {

    fun process(queuedEvents: List<NotifiableEvent>, currentRoomId: String?, currentThreadId: String?, renderedEvents: ProcessedEvents): ProcessedEvents {
        val processedEvents = queuedEvents.map {
            val type = when (it) {
                is InviteNotifiableEvent -> if (autoAcceptInvites.hideInvites) REMOVE else KEEP
                is NotifiableMessageEvent -> when {
                    it.shouldIgnoreMessageEventInRoom(currentRoomId, currentThreadId) -> REMOVE
                    outdatedDetector.isMessageOutdated(it) -> REMOVE
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
}