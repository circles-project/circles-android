package org.futo.circles.feature.notices

import androidx.lifecycle.map
import org.futo.circles.feature.timeline.BaseTimelineViewModel
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.model.PostContentType
import org.futo.circles.model.RootPost
import org.futo.circles.model.SystemNoticeListItem
import org.futo.circles.model.TextContent

class SystemNoticesTimelineViewModel(
    timelineDataSource: TimelineDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val timelineEventsLiveData = timelineDataSource.timelineEventsLiveData.map { list ->
        list.mapNotNull {
            if (it is RootPost && it.content.type == PostContentType.TEXT_CONTENT)
                SystemNoticeListItem(
                    it.id,
                    (it.content as? TextContent)?.message?.toString() ?: "",
                    it.postInfo.timestamp
                )
            else null
        }
    }

}