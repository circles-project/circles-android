package org.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(private val dataSource: GroupsDataSource) : ViewModel() {

    val roomsLiveData = dataSource.getGroupsLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }

    fun acceptGroupInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.acceptInvite(roomId)) }
    }
}