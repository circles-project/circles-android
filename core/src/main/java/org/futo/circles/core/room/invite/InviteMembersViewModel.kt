package org.futo.circles.core.room.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.futo.circles.core.ErrorParser
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class InviteMembersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: InviteRequestsDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val titleLiveData = MutableLiveData(getInviteTitle(roomId))

    val inviteResultLiveData = SingleEventLiveData<Response<Any>>()

    fun invite(usersIds: List<String>) {
        launchBg(
            CoroutineExceptionHandler { _, t ->
                inviteResultLiveData.postValue(Response.Error(ErrorParser.getErrorMessage(t)))
            }
        ) { inviteResultLiveData.postValue(dataSource.inviteUsers(this, roomId, usersIds)) }
    }

    private fun getInviteTitle(roomId: String) =
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.roomSummary()?.nameOrId() ?: roomId

}