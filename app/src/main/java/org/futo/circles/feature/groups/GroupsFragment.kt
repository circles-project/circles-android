package org.futo.circles.feature.groups

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.base.list.GridOffsetDecoration
import org.futo.circles.core.extensions.dpToPx
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.FragmentGroupsBinding
import org.futo.circles.feature.groups.list.GroupListItemViewType
import org.futo.circles.feature.groups.list.GroupsListAdapter
import org.futo.circles.feature.groups.list.JoinedGroupViewHolder
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
            layoutManager = GridLayoutManager(requireContext(), 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                        when ((adapter as? GroupsListAdapter)?.getItemViewType(position)) {
                            GroupListItemViewType.InviteNotification.ordinal -> 2
                            else -> 1
                        }
                }
            }
            addItemDecoration(getGroupGridOffsetDecoration())

            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.groups_empty_message))
            })
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

    private fun getGroupGridOffsetDecoration() = GridOffsetDecoration { holder, position ->
        if (holder !is JoinedGroupViewHolder) return@GridOffsetDecoration Rect()
        val itemNumber = position + 1
        var left = 0
        var right = 0
        val defaultOffset = requireContext().dpToPx(10)
        if ((listAdapter as? GroupsListAdapter)?.getItemViewType(0) == GroupListItemViewType.InviteNotification.ordinal) {
            if (itemNumber % 2 == 0) {
                left = defaultOffset
                right = 0
            } else {
                left = 0
                right = defaultOffset
            }
        } else {
            if (itemNumber % 2 == 0) {
                left = 0
                right = defaultOffset
            } else {
                left = defaultOffset
                right = 0
            }
        }

        Rect(left, 0, right, 0)
    }
}