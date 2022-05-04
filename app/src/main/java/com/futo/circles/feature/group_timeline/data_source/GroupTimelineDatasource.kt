package com.futo.circles.feature.group_timeline.data_source

import android.content.Context
import com.futo.circles.core.matrix.timeline.data_source.BaseTimelineDataSource
import com.futo.circles.core.matrix.timeline.data_source.GroupTimelineBuilder
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider

class GroupTimelineDatasource(
    private val roomId: String,
    context: Context,
    timelineBuilder: GroupTimelineBuilder
) : BaseTimelineDataSource(roomId, context, timelineBuilder) {

    suspend fun leaveGroup() =
        createResult { MatrixSessionProvider.currentSession?.leaveRoom(roomId) }

}