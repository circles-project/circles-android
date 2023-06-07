package org.futo.circles.feature.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.bindToFab
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.databinding.FragmentRoomsBinding
import org.futo.circles.feature.groups.list.GroupsListAdapter
import org.futo.circles.feature.home.HomeViewModel
import org.futo.circles.model.GroupListItem

@AndroidEntryPoint
class GroupsFragment : Fragment(R.layout.fragment_rooms) {

    private val viewModel by viewModels<GroupsViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)
    private val listAdapter by lazy {
        GroupsListAdapter(
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
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

    private fun onInviteClicked(room: GroupListItem, isAccepted: Boolean) {
        if (isAccepted) viewModel.acceptGroupInvite(room.id)
        else viewModel.rejectInvite(room.id)
    }

    private fun onRoomListItemClicked(room: GroupListItem) {
        findNavController().navigate(GroupsFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateRoom() {
        findNavController().navigate(GroupsFragmentDirections.toCreateGroupDialogFragment())
    }
}