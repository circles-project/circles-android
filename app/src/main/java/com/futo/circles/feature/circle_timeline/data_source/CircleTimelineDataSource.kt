package com.futo.circles.feature.circle_timeline.data_source

import android.content.Context
import com.futo.circles.feature.timeline.data_source.BaseTimelineDataSource
import com.futo.circles.feature.timeline.data_source.TimelineBuilder
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.Room

class CircleTimelineDataSource(
    roomId: String,
    context: Context,
    timelineBuilder: TimelineBuilder
) : BaseTimelineDataSource(roomId, context, timelineBuilder) {

    override fun getTimelineRooms(): List<Room> = room?.roomSummary()?.spaceChildren?.mapNotNull {
        MatrixSessionProvider.currentSession?.getRoom(it.childRoomId)
    } ?: emptyList()
    
}