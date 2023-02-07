package org.futo.circles.feature.circles

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import org.futo.circles.core.rooms.HasInvites
import org.futo.circles.core.rooms.RoomsFragment
import org.futo.circles.extensions.observeData
import org.futo.circles.feature.groups.GroupsFragmentDirections
import org.futo.circles.feature.home.HomeViewModel
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CirclesFragment : RoomsFragment(), HasInvites {

    override val viewModel by viewModel<CirclesViewModel>()
    private val homeViewModel by activityViewModel<HomeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.notificationLiveData.observeData(this) {
            findNavController().navigate(GroupsFragmentDirections.toTimeline(it))
        }
    }

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