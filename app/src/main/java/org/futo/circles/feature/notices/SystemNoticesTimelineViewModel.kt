package org.futo.circles.feature.notices

import androidx.lifecycle.map
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.TimelineDataSource
import org.futo.circles.core.model.PostContentType
import org.futo.circles.model.SystemNoticeListItem
import org.futo.circles.core.model.TextContent

class SystemNoticesTimelineViewModel(
    timelineDataSource: TimelineDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val timelineEventsLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull {
            if (it.content.type == PostContentType.TEXT_CONTENT)
                SystemNoticeListItem(
                    it.id,
                    (it.content as? TextContent)?.message ?: "",
                    it.postInfo.timestamp
                )
            else null
        }
    }

}