package org.futo.circles.feature.groups

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.rooms.HasInvites
import org.futo.circles.core.rooms.RoomsFragment
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupsFragment : RoomsFragment(), HasInvites {

    override val viewModel by viewModel<GroupsViewModel>()

    override fun onRoomListItemClicked(room: RoomListItem) {
        findNavController().navigate(GroupsFragmentDirections.toTimeline(room.id))
    }

    override fun navigateToCreateRoom() {
        findNavController().navigate(GroupsFragmentDirections.toCreateGroupDialogFragment())
    }

    override fun onAcceptInviteClicked(room: RoomListItem) {
        viewModel.acceptGroupInvite(room.id)
    }
}