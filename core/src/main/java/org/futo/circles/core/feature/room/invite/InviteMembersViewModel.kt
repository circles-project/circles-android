package org.futo.circles.core.feature.room.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.model.InviteLoadingEvent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class InviteMembersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: ManageInviteRequestsDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val titleLiveData = MutableLiveData(getInviteTitle(roomId))
    val inviteResultLiveData = SingleEventLiveData<Response<Any>>()
    val inviteLoadingEventLiveData = SingleEventLiveData<InviteLoadingEvent>()

    fun invite(usersIds: List<String>) = launchBg {
        usersIds.forEach { userId ->
            inviteLoadingEventLiveData.postValue(
                InviteLoadingEvent(userId, getInviteTitle(roomId))
            )
            val result = dataSource.inviteUser(roomId, userId)
            (result as? Response.Error)?.let {
                inviteLoadingEventLiveData.postValue(InviteLoadingEvent(isLoading = false))
                inviteResultLiveData.postValue(result)
                return@launchBg
            }
        }
        inviteLoadingEventLiveData.postValue(InviteLoadingEvent(isLoading = false))
        inviteResultLiveData.postValue(Response.Success(Unit))
    }

    private fun getInviteTitle(roomId: String) =
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.roomSummary()?.nameOrId() ?: roomId

}