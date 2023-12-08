package org.futo.circles.core.feature.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.mapping.nameOrId

abstract class BaseTimelineViewModel(
    private val baseTimelineDataSource: BaseTimelineDataSource
) : ViewModel() {

    val titleLiveData =
        baseTimelineDataSource.room.getRoomSummaryLive().map { it.getOrNull()?.nameOrId() ?: "" }

    val timelineEventsLiveData = baseTimelineDataSource.getTimelineEventFlow().asLiveData()

    override fun onCleared() {
        baseTimelineDataSource.clearTimeline()
        super.onCleared()
    }

    fun loadMore(): Boolean = baseTimelineDataSource.loadMore()
}