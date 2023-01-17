package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.model.NotifiableEvent

class NotificationState(
        private val queuedEvents: NotificationEventQueue,
        private val renderedEvents: MutableList<ProcessedEvent<NotifiableEvent>>
) {

    fun <T> updateQueuedEvents(
            drawerManager: NotificationDrawerManager,
            action: NotificationDrawerManager.(NotificationEventQueue, List<ProcessedEvent<NotifiableEvent>>) -> T
    ): T {
        return synchronized(queuedEvents) {
            action(drawerManager, queuedEvents, renderedEvents)
        }
    }

    fun clearAndAddRenderedEvents(eventsToRender: List<ProcessedEvent<NotifiableEvent>>) {
        renderedEvents.clear()
        renderedEvents.addAll(eventsToRender)
    }

    fun hasAlreadyRendered(eventsToRender: List<ProcessedEvent<NotifiableEvent>>) = renderedEvents == eventsToRender

    fun queuedEvents(block: (NotificationEventQueue) -> Unit) {
        synchronized(queuedEvents) {
            block(queuedEvents)
        }
    }
}
