package org.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class GroupsViewModel(private val dataSource: GroupsDataSource) : ViewModel() {

    val roomsLiveData = dataSource.getGroupsLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }

    fun acceptGroupInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.acceptInvite(roomId)) }
    }
}