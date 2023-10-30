package org.futo.circles.core.feature.room.knoks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.InviteRequestsDataSource
import org.futo.circles.core.model.KnockRequestListItem
import javax.inject.Inject

@HiltViewModel
class KnockRequestViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val inviteRequestsDataSource: InviteRequestsDataSource,
    knockRequestsDataSource: KnockRequestsDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val knockRequestsLiveData = knockRequestsDataSource.getKnockRequestsListItemsLiveData(roomId)
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun inviteUser(user: KnockRequestListItem) {
        launchBg {
            val result = inviteRequestsDataSource.inviteUser(roomId, user.requesterId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun kickUser(user: KnockRequestListItem) {
        launchBg { inviteRequestsDataSource.kickUser(roomId, user.requesterId) }
    }

}
