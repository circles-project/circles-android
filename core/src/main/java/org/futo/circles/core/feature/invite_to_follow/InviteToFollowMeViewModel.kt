package org.futo.circles.core.feature.invite_to_follow

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.model.SelectableRoomListItem
import javax.inject.Inject

@HiltViewModel
class InviteToFollowMeViewModel @Inject constructor(

) : ViewModel() {
    fun invite(selectedRooms: List<SelectableRoomListItem>) {

    }

}