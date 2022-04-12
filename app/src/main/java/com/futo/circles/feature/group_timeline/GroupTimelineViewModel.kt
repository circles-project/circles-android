package com.futo.circles.feature.group_timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineDatasource

class GroupTimelineViewModel(
    private val dataSource: GroupTimelineDatasource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getGroupTitle())
    val timelineEventsLiveData = dataSource.timelineEventsLiveData
    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()

    init {
        dataSource.startTimeline()
    }

    fun loadMore() {
        dataSource.loadMore()
    }

    fun toggleRepliesVisibilityFor(eventId: String) {
        dataSource.toggleRepliesVisibility(eventId)
    }

    fun leaveGroup() {
        launchBg { leaveGroupLiveData.postValue(dataSource.leaveGroup()) }
    }

    override fun onCleared() {
        dataSource.clearTimeline()
        super.onCleared()
    }

}