package org.futo.circles.feature.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.list.RoomsDividerDecoration
import org.futo.circles.core.rooms.list.RoomListItemViewType
import org.futo.circles.core.rooms.list.RoomsListAdapter
import org.futo.circles.databinding.FragmentRoomsBinding
import org.futo.circles.extensions.bindToFab
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.feature.home.HomeViewModel
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GroupsFragment : Fragment(R.layout.fragment_rooms) {

    private val viewModel by viewModel<GroupsViewModel>()
    private val homeViewModel by activityViewModel<HomeViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)
    private val listAdapter by lazy {
        RoomsListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            addItemDecoration(RoomsDividerDecoration(context, RoomListItemViewType.Gallery.ordinal))
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        homeViewModel.notificationLiveData.observeData(this) {
            findNavController().navigate(GroupsFragmentDirections.toTimeline(it))
        }
        viewModel.roomsLiveData?.observeData(this) { listAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun onInviteClicked(room: RoomListItem, isAccepted: Boolean) {
        if (isAccepted) viewModel.acceptGroupInvite(room.id)
        else viewModel.rejectInvite(room.id)
    }

    private fun onRoomListItemClicked(room: RoomListItem) {
        findNavController().navigate(GroupsFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateRoom() {
        findNavController().navigate(GroupsFragmentDirections.toCreateGroupDialogFragment())
    }
}