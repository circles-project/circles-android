package com.futo.circles.feature.circle_timeline.data_source

import android.content.Context
import com.futo.circles.core.matrix.timeline.data_source.BaseTimelineDataSource
import com.futo.circles.core.matrix.timeline.data_source.GroupTimelineBuilder

class CircleTimelineDataSource(
    roomId: String,
    context: Context,
    timelineBuilder: GroupTimelineBuilder
) : BaseTimelineDataSource(roomId, context, timelineBuilder) {

}