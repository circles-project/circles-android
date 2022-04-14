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

    val titleLiveData = MutableLiveData(dataSource.getGroupTitle())
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

    fun isUserAbleToPost(powerContent: PowerLevelsContent): Boolean {
        val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
        val helper = PowerLevelsHelper(powerContent)
        return helper.isUserAllowedToSend(userId, false, EventType.MESSAGE)
    }

    fun isUserAbleToInvite(powerContent: PowerLevelsContent): Boolean {
        val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
        val helper = PowerLevelsHelper(powerContent)
        return helper.isUserAbleToInvite(userId)
    }

    fun isUserAbleToChangeSettings(powerContent: PowerLevelsContent): Boolean {
        val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
        val helper = PowerLevelsHelper(powerContent)
        return helper.isUserAbleToRedact(userId)
    }

    override fun onCleared() {
        dataSource.clearTimeline()
        super.onCleared()
    }

}