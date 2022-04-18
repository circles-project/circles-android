package com.futo.circles.feature.group_timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineDatasource
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper

class GroupTimelineViewModel(
    private val dataSource: GroupTimelineDatasource
) : ViewModel() {

    val titleLiveData = dataSource.roomTitleLiveData
    val timelineEventsLiveData = dataSource.timelineEventsLiveData
    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()
    val accessLevelLiveData = dataSource.accessLevelFlow.asLiveData()

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