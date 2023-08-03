package org.futo.circles.core.notices

import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.SystemNoticeListItem
import org.futo.circles.core.model.TextContent
import org.futo.circles.core.timeline.BaseTimelineViewModel
import org.futo.circles.core.timeline.data_source.SingleTimelineDataSource
import javax.inject.Inject

@HiltViewModel
class SystemNoticesTimelineViewModel @Inject constructor(
    timelineDataSource: SingleTimelineDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val systemNoticesTimelineEventsLiveData =
        timelineDataSource.timelineEventsLiveData.map { list ->
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