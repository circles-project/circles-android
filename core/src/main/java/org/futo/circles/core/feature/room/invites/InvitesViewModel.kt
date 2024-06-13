package org.futo.circles.core.feature.room.invites

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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
import org.futo.circles.core.model.InviteTypeArg
import org.futo.circles.core.model.RoomInviteListItem
import javax.inject.Inject

@HiltViewModel
class InvitesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: RoomRequestsDataSource,
    private val manageInviteRequestsDataSource: ManageInviteRequestsDataSource
) : ViewModel() {

    private val inviteType: InviteTypeArg = savedStateHandle.getOrThrow("type")

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()
    private val loadingItemsIdsList = MutableLiveData<Set<String>>(emptySet())
    val invitesLiveData = MediatorLiveData<List<RoomInviteListItem>>().also {
        it.addSource(loadingItemsIdsList) { loadingItemsValue ->
            val currentList = it.value ?: emptyList()
            it.postValue(currentList.map { item ->
                item.copy(isLoading = loadingItemsValue.contains(item.id))
            })
        }
        it.addSource(dataSource.getRoomInvitesFlow(inviteType).asLiveData()) { value ->
            it.postValue(value)
        }
    }

    fun getInviteType() = inviteType

    fun rejectRoomInvite(roomId: String) {
        launchBg {
            toggleItemLoading(roomId)
            val result = manageInviteRequestsDataSource.rejectInvite(roomId)
            postInviteResult(result, roomId)
        }
    }

    fun acceptRoomInvite(roomId: String, roomType: CircleRoomTypeArg) {
        launchBg {
            toggleItemLoading(roomId)
            val result = manageInviteRequestsDataSource.acceptInvite(roomId, roomType)
            postInviteResult(result, roomId)
        }
    }

    fun unblurProfileIcon(roomId: String) {
        dataSource.unblurProfileImageFor(roomId)
    }

    private fun postInviteResult(result: Response<Unit?>, id: String) {
        inviteResultLiveData.postValue(result)
        toggleItemLoading(id)
    }

    private fun toggleItemLoading(id: String) {
        val currentSet = loadingItemsIdsList.value?.toMutableSet() ?: return
        val newLoadingSet = currentSet.apply {
            if (this.contains(id)) remove(id)
            else add(id)
        }
        loadingItemsIdsList.postValue(newLoadingSet)
    }

}