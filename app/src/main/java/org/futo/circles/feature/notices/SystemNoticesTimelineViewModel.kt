package org.futo.circles.feature.notices

import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.TextContent
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.TimelineDataSource
import org.futo.circles.model.SystemNoticeListItem
import javax.inject.Inject

@HiltViewModel
class SystemNoticesTimelineViewModel @Inject constructor(
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