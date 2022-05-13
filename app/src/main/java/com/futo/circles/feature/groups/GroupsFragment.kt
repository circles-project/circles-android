package com.futo.circles.feature.groups

import androidx.navigation.fragment.findNavController
import com.futo.circles.core.rooms.RoomsFragment
import com.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupsFragment : RoomsFragment() {

    override val viewModel by viewModel<GroupsViewModel>()

    override fun onRoomListItemClicked(room: RoomListItem) {
        findNavController().navigate(GroupsFragmentDirections.toTimeline(room.id))
    }

    override fun navigateToCreateRoom() {
        findNavController().navigate(GroupsFragmentDirections.toCreateRoomDialogFragment())
    }

    override fun onAcceptInviteClicked(room: RoomListItem) {
        viewModel.acceptGroupInvite(room.id)
    }
}