package org.futo.circles.feature.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.FragmentGroupsBinding
import org.futo.circles.feature.groups.list.GroupsListAdapter
import org.futo.circles.model.GroupListItem

@AndroidEntryPoint
class GroupsFragment : BaseBindingFragment<FragmentGroupsBinding>(FragmentGroupsBinding::inflate) {

    private val viewModel by viewModels<GroupsViewModel>()
    private val listAdapter by lazy {
        GroupsListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onOpenInvitesClicked = {
                findNavController().navigateSafe(GroupsFragmentDirections.toRoomRequests())
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
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.groups_empty_message))
                setArrowVisible(true)
            })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
        }
        binding.ivCreateGroup.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
    }


    private fun onRoomListItemClicked(room: GroupListItem) {
        findNavController().navigateSafe(GroupsFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateRoom() {
        findNavController().navigateSafe(GroupsFragmentDirections.toCreateGroupDialogFragment())
    }
}