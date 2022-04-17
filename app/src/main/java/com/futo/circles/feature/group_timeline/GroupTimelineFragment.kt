package com.futo.circles.feature.group_timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.list.BaseRvDecoration
import com.futo.circles.databinding.GroupTimelineFragmentBinding
import com.futo.circles.extensions.*
import com.futo.circles.feature.group_timeline.list.GroupPostViewHolder
import com.futo.circles.feature.group_timeline.list.GroupTimelineAdapter
import com.futo.circles.model.Post
import com.futo.circles.view.GroupPostListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent


class GroupTimelineFragment : Fragment(R.layout.group_timeline_fragment), GroupPostListener {

    private val args: GroupTimelineFragmentArgs by navArgs()
    private val viewModel by viewModel<GroupTimelineViewModel> { parametersOf(args.roomId) }
    private val binding by viewBinding(GroupTimelineFragmentBinding::bind)
    private var isSettingAvailable = false
    private var isInviteAvailable = false

    private val listAdapter by lazy {
        GroupTimelineAdapter(this) { viewModel.loadMore() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.rvGroupTimeline.apply {
            adapter = listAdapter
            addItemDecoration(
                BaseRvDecoration.OffsetDecoration<GroupPostViewHolder>(
                    offset = context.dimen(R.dimen.group_post_item_offset)
                )
            )
            bindToFab(binding.fbCreatePost)
        }
        setupObservers()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.group_timeline_menu, menu)
        menu.findItem(R.id.settings).isVisible = isSettingAvailable
        menu.findItem(R.id.inviteMembers).isVisible = isInviteAvailable
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.manageMembers -> {
                navigateToManageMembers()
                return true
            }
            R.id.inviteMembers -> {
                navigateToInviteMembers()
                return true
            }
            R.id.leaveGroup -> {
                showLeaveGroupDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObservers() {
        with(viewModel) {
            titleLiveData.observeData(this@GroupTimelineFragment) { title -> setToolbarTitle(title) }
            timelineEventsLiveData.observeData(this@GroupTimelineFragment, ::setTimelineList)
            leaveGroupLiveData.observeResponse(this@GroupTimelineFragment,
                success = { activity?.onBackPressed() })
            accessLevelLiveData.observeData(this@GroupTimelineFragment) { powerContent ->
                handleAccessActionsVisibility(powerContent)
            }
        }
    }

    private fun setTimelineList(list: List<Post>) {
        listAdapter.submitList(list)
    }

    override fun onShowRepliesClicked(eventId: String) {
        viewModel.toggleRepliesVisibilityFor(eventId)
    }

    private fun showLeaveGroupDialog() {
        showDialog(
            titleResIdRes = R.string.leave_group,
            messageResId = R.string.leave_group_message,
            positiveButtonRes = R.string.leave,
            negativeButtonVisible = true,
            positiveAction = { viewModel.leaveGroup() }
        )
    }

    private fun handleAccessActionsVisibility(powerContent: PowerLevelsContent) {
        binding.fbCreatePost.setIsVisible(viewModel.isUserAbleToPost(powerContent))
        isSettingAvailable = viewModel.isUserAbleToChangeSettings(powerContent)
        isInviteAvailable = viewModel.isUserAbleToInvite(powerContent)
        activity?.invalidateOptionsMenu()
    }

    private fun navigateToInviteMembers() {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toInviteMembersDialogFragment(args.roomId)
        )
    }

    private fun navigateToManageMembers() {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toManageMembersDialogFragment(args.roomId)
        )
    }
}