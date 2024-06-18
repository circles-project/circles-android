package org.futo.circles.core.feature.invite_to_follow

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.invite.ManageInviteRequestsDataSource
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.model.InviteLoadingEvent
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.utils.getTimelineRoomFor
import javax.inject.Inject

@HiltViewModel
class InviteToFollowMeViewModel @Inject constructor(
    private val manageInviteRequestsDataSource: ManageInviteRequestsDataSource
) : ViewModel() {

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val inviteLoadingEventLiveData = SingleEventLiveData<InviteLoadingEvent>()

    fun invite(userId: String, selectedRooms: List<SelectableRoomListItem>) {
        val timelines = selectedRooms.mapNotNull { getTimelineRoomFor(it.id) }
        launchBg {
            timelines.forEach { timeline ->
                inviteLoadingEventLiveData.postValue(
                    InviteLoadingEvent(
                        userId,
                        timeline.roomSummary()?.nameOrId() ?: ""
                    )
                )
                val result = manageInviteRequestsDataSource.inviteUser(timeline.roomId, userId)
                (result as? Response.Error)?.let {
                    inviteLoadingEventLiveData.postValue(InviteLoadingEvent(isLoading = false))
                    inviteResultLiveData.postValue(result)
                    return@launchBg
                }
            }
            inviteLoadingEventLiveData.postValue(InviteLoadingEvent(isLoading = false))
            inviteResultLiveData.postValue(Response.Success(Unit))
        }
    }
}