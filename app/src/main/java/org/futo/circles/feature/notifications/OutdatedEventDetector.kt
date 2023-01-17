package org.futo.circles.feature.notifications

import org.futo.circles.feature.notifications.model.NotifiableEvent
import org.futo.circles.feature.notifications.model.NotifiableMessageEvent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom

class OutdatedEventDetector {

    fun isMessageOutdated(notifiableEvent: NotifiableEvent): Boolean {
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
