package com.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.groups.data_source.GroupsDataSource
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class GroupsViewModel(
    private val dataSource: GroupsDataSource
) : ViewModel() {

    val groupsLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> dataSource.filterGroups(list) }

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun acceptInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.acceptInvite(roomId)) }
    }

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }
}
