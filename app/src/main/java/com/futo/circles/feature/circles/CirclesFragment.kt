package com.futo.circles.feature.circles

import androidx.navigation.fragment.findNavController
import com.futo.circles.core.rooms.RoomsFragment
import com.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class CirclesFragment : RoomsFragment() {

    override val viewModel by viewModel<CirclesViewModel>()

    override fun onRoomListItemClicked(room: RoomListItem) {
        findNavController().navigate(CirclesFragmentDirections.toTimeline(room.id))
    }

    override fun navigateToCreateRoom() {
        findNavController().navigate(CirclesFragmentDirections.toCreateRoomDialogFragment())
    }

    override fun onAcceptInviteClicked(room: RoomListItem) {

    }
}