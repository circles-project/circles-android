package org.futo.circles.core.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.timeline.data_source.BaseTimelineDataSource

abstract class BaseTimelineViewModel(
    private val baseTimelineDataSource: BaseTimelineDataSource
) : ViewModel() {

    val titleLiveData =
        baseTimelineDataSource.room.getRoomSummaryLive().map { it.getOrNull()?.nameOrId() ?: "" }

    val timelineEventsLiveData = baseTimelineDataSource.timelineEventsLiveData

    init {
        baseTimelineDataSource.startTimeline()
    }

    override fun onCleared() {
        baseTimelineDataSource.clearTimeline()
        super.onCleared()
    }

    fun loadMore() {
        baseTimelineDataSource.loadMore()
    }
}