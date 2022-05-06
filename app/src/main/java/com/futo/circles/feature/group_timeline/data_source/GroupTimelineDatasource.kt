package com.futo.circles.feature.group_timeline.data_source

import android.content.Context
import com.futo.circles.core.matrix.timeline.data_source.BaseTimelineDataSource
import com.futo.circles.core.matrix.timeline.data_source.GroupTimelineBuilder
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.Room

class GroupTimelineDatasource(
    private val roomId: String,
    context: Context,
    timelineBuilder: GroupTimelineBuilder
) : BaseTimelineDataSource(roomId, context, timelineBuilder) {

    override fun getTimelineRooms(): List<Room> = listOfNotNull(room)

    suspend fun leaveGroup() =
        createResult { MatrixSessionProvider.currentSession?.leaveRoom(roomId) }

}