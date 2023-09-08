package org.futo.circles.feature.circles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.room.invite.InviteRequestsDataSource
import org.futo.circles.model.RequestCircleListItem
import javax.inject.Inject

@HiltViewModel
class CirclesViewModel @Inject constructor(
    private val dataSource: CirclesDataSource,
    private val inviteRequestsDataSource: InviteRequestsDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getCirclesFlow().asLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg {
            val result = inviteRequestsDataSource.rejectInvite(roomId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun inviteUser(room: RequestCircleListItem) {
        launchBg {
            val result = inviteRequestsDataSource.inviteUser(room.id, room.requesterId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun kickUser(room: RequestCircleListItem) {
        launchBg { inviteRequestsDataSource.kickUser(room.id, room.requesterId) }
    }

}