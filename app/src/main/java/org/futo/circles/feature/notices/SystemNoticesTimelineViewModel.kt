package org.futo.circles.feature.notices

import org.futo.circles.feature.timeline.BaseTimelineViewModel
import org.futo.circles.feature.timeline.data_source.TimelineDataSource

class SystemNoticesTimelineViewModel(
    timelineDataSource: TimelineDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val timelineEventsLiveData = timelineDataSource.timelineEventsLiveData

}