package org.futo.circles.feature.circles

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.rooms.HasInvites
import org.futo.circles.core.rooms.RoomsFragment
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class CirclesFragment : RoomsFragment(), HasInvites {

    override val viewModel by viewModel<CirclesViewModel>()

    override fun onRoomListItemClicked(room: RoomListItem) {
        findNavController().navigate(CirclesFragmentDirections.toTimeline(room.id))
    }

    override fun navigateToCreateRoom() {
        findNavController().navigate(CirclesFragmentDirections.toCreateCircleDialogFragment())
    }

    override fun onAcceptInviteClicked(room: RoomListItem) {
        findNavController().navigate(
            CirclesFragmentDirections.toAcceptCircleInviteDialogFragment(room.id)
        )
    }
}