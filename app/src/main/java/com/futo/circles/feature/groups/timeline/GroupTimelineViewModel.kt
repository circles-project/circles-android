package com.futo.circles.feature.groups.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.groups.timeline.data_source.GroupTimelineDatasource

class GroupTimelineViewModel(
    private val dataSource: GroupTimelineDatasource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getGroupTitle())
    val timelineEventsLiveData = dataSource.timelineEventsLiveData

    init {
        dataSource.startTimeline()
    }

    fun loadMore() {
        dataSource.loadMore()
    }

    fun toggleRepliesVisibilityFor(eventId: String) {
        dataSource.toggleRepliesVisibility(eventId)
    }

    override fun onCleared() {
        dataSource.clearTimeline()
        super.onCleared()
    }

}