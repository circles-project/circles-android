package org.futo.circles.core.feature.room.invites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.InviteRequestsDataSource
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.InviteTypeArg
import javax.inject.Inject

@HiltViewModel
class InvitesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: InvitesDataSource,
    private val inviteRequestsDataSource: InviteRequestsDataSource
) : ViewModel() {

    private val inviteType: InviteTypeArg = savedStateHandle.getOrThrow("type")

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val invitesLiveData = dataSource.getInvitesFlow(inviteType).asLiveData()

    fun getInviteType() = inviteType

    fun rejectRoomInvite(roomId: String) {
        launchBg {
            val result = inviteRequestsDataSource.rejectInvite(roomId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun acceptRoomInvite(roomId: String, roomType: CircleRoomTypeArg) {
        launchBg {
            val result = inviteRequestsDataSource.acceptInvite(roomId, roomType)
            inviteResultLiveData.postValue(result)
        }
    }


    fun onFollowRequestAnswered(userId: String, accepted: Boolean) {
        launchBg {
            val result = if (accepted) dataSource.acceptFollowRequest(userId)
            else dataSource.declineFollowRequest(userId)
            inviteResultLiveData.postValue(result)
        }
    }


    fun unblurProfileIcon(roomId: String) {
        dataSource.unblurProfileImageFor(roomId)
    }


}