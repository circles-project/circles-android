package org.futo.circles.feature.circles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.databinding.FragmentRoomsBinding
import org.futo.circles.extensions.bindToFab
import org.futo.circles.feature.circles.list.CirclesListAdapter
import org.futo.circles.feature.home.HomeViewModel
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.RequestCircleListItem
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CirclesFragment : Fragment(R.layout.fragment_rooms) {

    private val viewModel by viewModel<CirclesViewModel>()
    private val homeViewModel by activityViewModel<HomeViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)
    private val listAdapter by lazy {
        CirclesListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            },
            onRequestClicked = { roomListItem, isAccepted ->
                onRequestClicked(roomListItem, isAccepted)
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
            findNavController().navigate(CirclesFragmentDirections.toTimeline(it))
        }
        viewModel.roomsLiveData?.observeData(this) { listAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun onInviteClicked(room: CircleListItem, isAccepted: Boolean) {
        if (isAccepted) onAcceptInviteClicked(room)
        else viewModel.rejectInvite(room.id)
    }

    private fun onRequestClicked(room: RequestCircleListItem, isAccepted: Boolean) {
        if (isAccepted) viewModel.inviteUser(room)
        else viewModel.kickUser(room)
    }

    private fun onRoomListItemClicked(room: CircleListItem) {
        findNavController().navigate(CirclesFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateRoom() {
        findNavController().navigate(CirclesFragmentDirections.toCreateCircleDialogFragment())
    }

    private fun onAcceptInviteClicked(room: CircleListItem) {
        findNavController().navigate(
            CirclesFragmentDirections.toAcceptCircleInviteDialogFragment(room.id)
        )
    }
}