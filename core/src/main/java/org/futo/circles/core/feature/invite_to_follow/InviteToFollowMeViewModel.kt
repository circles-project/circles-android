package org.futo.circles.core.feature.invite_to_follow

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.ManageInviteRequestsDataSource
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.utils.getTimelineRoomFor
import javax.inject.Inject

@HiltViewModel
class InviteToFollowMeViewModel @Inject constructor(
    private val manageInviteRequestsDataSource: ManageInviteRequestsDataSource
) : ViewModel() {

    val inviteResultLiveData = SingleEventLiveData<Response<Any>>()

    fun invite(userId: String, selectedRooms: List<SelectableRoomListItem>) {
        val timelinesIds = selectedRooms.mapNotNull { getTimelineRoomFor(it.id)?.roomId }
        launchBg {
            timelinesIds.forEach { timelineId ->
                try {
                    manageInviteRequestsDataSource.inviteUser(timelineId, userId)
                } catch (t: Throwable) {
                    inviteResultLiveData.postValue(
                        Response.Error(
                            t.message ?: "Failed to invite $userId to $timelineId"
                        )
                    )
                    return@launchBg
                }
            }
            inviteResultLiveData.postValue(Response.Success(Unit))
        }
    }
}