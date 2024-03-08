package org.futo.circles.core.feature.room.knoks

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.ManageInviteRequestsDataSource
import org.futo.circles.core.model.KnockRequestListItem
import javax.inject.Inject

@HiltViewModel
class KnockRequestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val manageInviteRequestsDataSource: ManageInviteRequestsDataSource,
    knockRequestsDataSource: KnockRequestsDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()
    private val loadingItemsIdsList = MutableLiveData<Set<String>>(emptySet())

    val knockRequestsLiveData = MediatorLiveData<List<KnockRequestListItem>>().also {
        it.addSource(loadingItemsIdsList) { loadingItemsValue ->
            val currentList = it.value ?: emptyList()
            it.postValue(currentList.map { item ->
                item.copy(isLoading = loadingItemsValue.contains(item.id))
            })
        }
        it.addSource(knockRequestsDataSource.getKnockRequestsListItemsLiveData(roomId)) { value ->
            it.postValue(value)
        }
    }

    fun inviteUser(user: KnockRequestListItem) {
        launchBg {
            toggleItemLoading(user.id)
            val result = manageInviteRequestsDataSource.inviteUser(roomId, user.requesterId)
            inviteResultLiveData.postValue(result)
            toggleItemLoading(user.id)
        }
    }

    fun kickUser(user: KnockRequestListItem) {
        launchBg {
            toggleItemLoading(user.id)
            manageInviteRequestsDataSource.kickUser(roomId, user.requesterId)
            toggleItemLoading(user.id)
        }
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
