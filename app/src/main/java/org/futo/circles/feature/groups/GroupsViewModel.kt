package org.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.InviteRequestsDataSource
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.model.GroupListItem
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val dataSource: GroupsDataSource,
    private val inviteRequestsDataSource: InviteRequestsDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGroupsFlow().asLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg {
            val result = inviteRequestsDataSource.rejectInvite(roomId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun acceptGroupInvite(roomId: String) {
        launchBg {
            val result = inviteRequestsDataSource.acceptInvite(roomId, CircleRoomTypeArg.Group)
            inviteResultLiveData.postValue(result)
        }
    }

    fun unblurProfileIcon(roomListItem: GroupListItem) {
        dataSource.unblurProfileImageFor(roomListItem.id)
    }
}