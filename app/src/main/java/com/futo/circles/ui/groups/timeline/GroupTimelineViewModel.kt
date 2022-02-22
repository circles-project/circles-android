package com.futo.circles.ui.groups.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.ui.groups.timeline.data_source.GroupTimelineDatasource

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

    override fun onCleared() {
        dataSource.clearTimeline()
        super.onCleared()
    }

}