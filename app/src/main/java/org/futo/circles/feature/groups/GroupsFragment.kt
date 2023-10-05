package org.futo.circles.feature.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.auth.explanation.CirclesExplanationDialog
import org.futo.circles.core.NetworkObserver
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.feature.groups.list.GroupsListAdapter
import org.futo.circles.model.GroupListItem
import org.futo.circles.model.RequestGroupListItem

@AndroidEntryPoint
class GroupsFragment : Fragment(org.futo.circles.core.R.layout.fragment_rooms) {

    private val viewModel by viewModels<GroupsViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }
    private val listAdapter by lazy {
        GroupsListAdapter(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (preferencesProvider.shouldShowExplanation(CircleRoomTypeArg.Group))
            CirclesExplanationDialog(requireContext(), CircleRoomTypeArg.Group).show()
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.groups_empty_message))
            })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it, listOf(binding.rvRooms)) }
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun onInviteClicked(room: GroupListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        if (isAccepted) viewModel.acceptGroupInvite(room.id)
        else viewModel.rejectInvite(room.id)
    }

    private fun onRequestClicked(room: RequestGroupListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        if (isAccepted) viewModel.inviteUser(room)
        else viewModel.kickUser(room)
    }

    private fun onRoomListItemClicked(room: GroupListItem) {
        findNavController().navigateSafe(GroupsFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateRoom() {
        findNavController().navigateSafe(GroupsFragmentDirections.toCreateGroupDialogFragment())
    }
}