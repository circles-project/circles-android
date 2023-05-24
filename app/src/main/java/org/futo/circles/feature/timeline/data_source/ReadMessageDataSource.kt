package org.futo.circles.feature.timeline.data_source

import kotlinx.coroutines.launch
import org.futo.circles.extensions.coroutineScope
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.read.ReadService
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class ReadMessageDataSource {

    private var mostRecentTimelineEvent: TimelineEvent? = null

    suspend fun markAsRead(roomId: String, eventId: String) {
        val room = MatrixSessionProvider.currentSession?.roomService()?.getRoom(roomId) ?: return
        updateMostRecentEvent(room, eventId)
        room.readService().setReadReceipt(eventId, ReadService.THREAD_ID_MAIN)
        room.readService().setReadMarker(eventId)
    }

    fun setReadMarker() {
        val session = MatrixSessionProvider.currentSession ?: return
        val event = mostRecentTimelineEvent ?: return
        val room = session.roomService().getRoom(event.roomId) ?: return
        session.coroutineScope.launch { room.readService().setReadMarker(event.eventId) }
    }

    private fun updateMostRecentEvent(room: Room, eventId: String) {
        val event = room.getTimelineEvent(eventId) ?: return
        val mostRecentEventTime = mostRecentTimelineEvent?.root?.originServerTs ?: 0L
        val currentEventTime = event.root.originServerTs ?: 0L
        if (currentEventTime > mostRecentEventTime) mostRecentTimelineEvent = event
    }
}