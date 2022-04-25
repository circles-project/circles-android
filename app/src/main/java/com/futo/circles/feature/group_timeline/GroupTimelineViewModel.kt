package com.futo.circles.feature.group_timeline

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineDatasource

class GroupTimelineViewModel(
    private val dataSource: GroupTimelineDatasource
) : ViewModel() {

    val titleLiveData = dataSource.roomTitleLiveData
    val timelineEventsLiveData = dataSource.timelineEventsLiveData
    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()
    val accessLevelLiveData = dataSource.accessLevelFlow.asLiveData()
    val scrollToTopLiveData = SingleEventLiveData<Unit>()

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

    fun sendTextPost(message: String, threadEventId: String?) {
        dataSource.sendTextMessage(message)
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
    }

    fun sendImagePost(uri: Uri, threadEventId: String?) {
        dataSource.sendImage(uri, threadEventId)
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
    }

}