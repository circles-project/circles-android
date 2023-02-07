package org.futo.circles.feature.groups

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.rooms.HasInvites
import org.futo.circles.core.rooms.RoomsFragment
import org.futo.circles.extensions.observeData
import org.futo.circles.feature.home.HomeViewModel
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupsFragment : RoomsFragment(), HasInvites {

    override val viewModel by viewModel<GroupsViewModel>()
    private val homeViewModel by activityViewModel<HomeViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.notificationLiveData.observeData(this){
            findNavController().navigate(GroupsFragmentDirections.toTimeline(it))
        }
    }
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