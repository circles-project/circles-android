package org.futo.circles.core.feature.room.requests

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.ManageInviteRequestsDataSource
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.KnockRequestListItem
import javax.inject.Inject

@HiltViewModel
class RoomRequestsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val requestsDataSource: RoomRequestsDataSource,
    private val manageInviteRequestsDataSource: ManageInviteRequestsDataSource
) : ViewModel() {

    private val inviteType: CircleRoomTypeArg = savedStateHandle.getOrThrow("type")
    private val roomId: String? = savedStateHandle["roomId"]

    val requestResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val requestsLiveData = requestsDataSource.getRequestsFlow(inviteType, roomId).asLiveData()

    fun rejectRoomInvite(roomId: String) {
        launchBg {
            requestsDataSource.toggleItemLoading(roomId)
            val result = manageInviteRequestsDataSource.rejectInvite(roomId)
            postInviteResult(result, roomId)
        }
    }

    fun acceptRoomInvite(roomId: String, roomType: CircleRoomTypeArg) {
        launchBg {
            requestsDataSource.toggleItemLoading(roomId)
            val result = manageInviteRequestsDataSource.acceptInvite(roomId, roomType)
            postInviteResult(result, roomId)
        }
    }

    fun inviteUser(knockRequest: KnockRequestListItem) {
        roomId ?: return
        launchBg {
            requestsDataSource.toggleItemLoading(knockRequest.id)
            val result = manageInviteRequestsDataSource.inviteUser(roomId, knockRequest.requesterId)
            postInviteResult(result, knockRequest.id)
        }
    }

    fun kickUser(knockRequest: KnockRequestListItem) {
        roomId ?: return
        launchBg {
            requestsDataSource.toggleItemLoading(knockRequest.id)
            val result = manageInviteRequestsDataSource.kickUser(roomId, knockRequest.requesterId)
            postInviteResult(result, knockRequest.id)
        }
    }

    fun unblurProfileIcon(roomId: String) {
        requestsDataSource.unblurProfileImageFor(roomId)
    }

    private fun postInviteResult(result: Response<Unit?>, id: String) {
        requestResultLiveData.postValue(result)
        requestsDataSource.toggleItemLoading(id)
    }

}