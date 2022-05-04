package com.futo.circles.feature.group_timeline

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.matrix.timeline.BaseTimelineFragment
import com.futo.circles.extensions.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent


class GroupTimelineFragment : BaseTimelineFragment() {

    private val args: GroupTimelineFragmentArgs by navArgs()
    override val viewModel by viewModel<GroupTimelineViewModel> { parametersOf(args.roomId) }
    override val roomId by lazy { args.roomId }
    private var isSettingAvailable = false
    private var isInviteAvailable = false

    override fun setupObservers() {
        super.setupObservers()
        viewModel.leaveGroupLiveData.observeResponse(this,
            success = { activity?.onBackPressed() }
        )
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.group_timeline_menu, menu)
        menu.findItem(R.id.configureGroup).isVisible = isSettingAvailable
        menu.findItem(R.id.inviteMembers).isVisible = isInviteAvailable
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureGroup -> {
                navigateToConfigureGroup()
                return true
            }
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

    override fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.fbCreatePost.setIsVisible(powerLevelsContent.isCurrentUserAbleToPost())
        isSettingAvailable = powerLevelsContent.isCurrentUserAbleToChangeSettings()
        isInviteAvailable = powerLevelsContent.isCurrentUserAbleToInvite()
        activity?.invalidateOptionsMenu()
    }

    override fun navigateToCreatePost(userName: String?, eventId: String?) {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toCreatePostBottomSheet(userName, eventId)
        )
    }

    override fun navigateToEmojiPicker(eventId: String) {
        findNavController().navigate(GroupTimelineFragmentDirections.toEmojiBottomSheet(eventId))
    }

    override fun navigateToReport(roomId: String, eventId: String) {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toReportDialogFragment(args.roomId, eventId)
        )
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

    private fun navigateToConfigureGroup() {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toUpdateRoomDialogFragment(args.roomId)
        )
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
}