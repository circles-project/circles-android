package org.futo.circles.feature.timeline.data_source

import org.futo.circles.provider.MatrixSessionProvider

class ReadMessageDataSource {

    suspend fun markAsRead(roomId: String, eventId: String) {
        val room = MatrixSessionProvider.currentSession?.roomService()?.getRoom(roomId) ?: return
        val isEventRead = room.readService().isEventRead(eventId)
        if (!isEventRead) {
            room.readService().setReadReceipt(eventId)
            room.readService().setReadMarker(eventId)
        }
    }
}