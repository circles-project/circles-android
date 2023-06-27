package org.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RequestCircleListItem
import org.futo.circles.model.RequestGroupListItem
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(private val dataSource: GroupsDataSource) : ViewModel() {

    val roomsLiveData = dataSource.getGroupsLiveData()?.asLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }

    fun acceptGroupInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.acceptInvite(roomId)) }
    }

    fun inviteUser(room: RequestGroupListItem) {
        launchBg {
            val result = createResult {
                MatrixSessionProvider.currentSession?.getRoom(room.id)?.membershipService()
                    ?.invite(room.requesterId)
            }
            inviteResultLiveData.postValue(result)
        }
    }

    fun kickUser(room: RequestGroupListItem) {
        launchBg {
            MatrixSessionProvider.currentSession?.getRoom(room.id)?.membershipService()
                ?.remove(room.requesterId)
        }
    }
}