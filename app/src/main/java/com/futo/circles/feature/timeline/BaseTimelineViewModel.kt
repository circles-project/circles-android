package com.futo.circles.feature.timeline

import androidx.lifecycle.ViewModel
import com.futo.circles.feature.timeline.data_source.TimelineDataSource

abstract class BaseTimelineViewModel(
    private val timelineDataSource: TimelineDataSource
) : ViewModel() {

    val titleLiveData = timelineDataSource.roomTitleLiveData
    val timelineEventsLiveData = timelineDataSource.timelineEventsLiveData

    init {
        timelineDataSource.startTimeline()
    }

    override fun onCleared() {
        timelineDataSource.clearTimeline()
        super.onCleared()
    }

    fun loadMore() {
        timelineDataSource.loadMore()
    }
}