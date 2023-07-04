package org.futo.circles.core.timeline

import androidx.lifecycle.ViewModel

abstract class BaseTimelineViewModel(
    private val timelineDataSource: TimelineDataSource
) : ViewModel() {

    val titleLiveData = timelineDataSource.roomTitleLiveData

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