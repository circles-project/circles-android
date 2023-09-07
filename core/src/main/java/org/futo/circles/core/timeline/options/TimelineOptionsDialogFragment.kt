package org.futo.circles.core.timeline.options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.databinding.DialogFragmentTimelineOptionsBinding
import org.futo.circles.core.extensions.isCurrentUserAbleToChangeSettings
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.extensions.isCurrentUserOnlyAdmin
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.DeleteCircle
import org.futo.circles.core.model.DeleteGroup
import org.futo.circles.core.model.LeaveGroup
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.utils.getTimelineRoomFor

@AndroidEntryPoint
class TimelineOptionsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentTimelineOptionsBinding::inflate) {

    private val binding by lazy {
        getBinding() as DialogFragmentTimelineOptionsBinding
    }

    private val args: TimelineOptionsDialogFragmentArgs by navArgs()
    private val isGroupMode by lazy { args.type == CircleRoomTypeArg.Group }
    private val timelineId by lazy {
        if (isGroupMode) args.roomId
        else getTimelineRoomFor(args.roomId)?.roomId ?: throw IllegalArgumentException(
            "Timeline not found"
        )
    }

    private val viewModel by viewModels<TimelineOptionsViewModel>()
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }
    private val navigator by lazy { TimelineOptionsNavigator(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            lPushNotifications.setOnClickListener {
                viewModel.setNotificationsEnabled(!svPushNotifications.isChecked)
            }
            tvConfigure.apply {
                setText(
                    getString(if (isGroupMode) R.string.configure_group else R.string.configure_circle)
                )
                setOnClickListener { navigator.navigateToUpdateRoom(args.roomId, args.type) }
            }
            tvDelete.apply {
                text = getString(if (isGroupMode) R.string.delete_group else R.string.delete_circle)
                setOnClickListener {
                    withConfirmation(if (isGroupMode) DeleteGroup() else DeleteCircle()) {
                        viewModel.delete(isGroupMode)
                    }
                }
            }
            tvStateEvents.apply {
                setIsVisible(preferencesProvider.isDeveloperModeEnabled())
                setOnClickListener { navigator.navigateToStateEvents(timelineId) }
            }
            tvInviteMembers.apply {
                setText(getString(if (isGroupMode) R.string.invite_members else R.string.invite_followers))
                setOnClickListener { navigator.navigateToInviteMembers(timelineId) }
            }
            tvLeave.apply {
                setIsVisible(isGroupMode)
                setOnClickListener { showLeaveGroupDialog() }
            }
            tvShare.setOnClickListener { navigator.navigateToShareRoom(timelineId) }
            tvManageMembers.apply {
                setIsVisible(isGroupMode)
                setOnClickListener { navigator.navigateToManageMembers(timelineId, args.type) }
            }
            tvMyFollowers.apply {
                setIsVisible(isGroupMode.not())
                setOnClickListener { navigator.navigateToManageMembers(timelineId, args.type) }
            }
            tvPeopleImFollowing.apply {
                setIsVisible(isGroupMode.not())
                setOnClickListener { navigator.navigateToFollowing(args.roomId) }
            }
        }

    }

    private fun setupObservers() {
        viewModel.leaveDeleteEventLiveData.observeResponse(this,
            success = {
                val controller = findNavController()
                controller.previousBackStackEntry?.destination?.id?.let {
                    controller.popBackStack(it, true)
                } ?: controller.popBackStack()
            }
        )
        viewModel.accessLevelLiveData.observeData(this) { groupPowerLevelsContent ->
            if (!isGroupMode) return@observeData
            with(binding) {
                tvConfigure.setIsVisible(groupPowerLevelsContent.isCurrentUserAbleToChangeSettings())
                tvInviteMembers.setIsVisible(groupPowerLevelsContent.isCurrentUserAbleToInvite())
                tvDelete.setIsVisible(groupPowerLevelsContent.isCurrentUserOnlyAdmin(args.roomId))
            }
        }
        viewModel.roomSummaryLiveData?.observeData(this) {
            it.getOrNull()?.let { room ->
                binding.ivCover.loadProfileIcon(room.avatarUrl, room.displayName)
                binding.toolbar.title = room.displayName
            }
        }
        viewModel.notificationsStateLiveData.observeData(this) {
            binding.svPushNotifications.isChecked = it
        }
    }


    private fun showLeaveGroupDialog() {
        if (viewModel.canLeaveRoom()) {
            withConfirmation(LeaveGroup()) { viewModel.leaveGroup() }
        } else {
            showDialog(
                titleResIdRes = R.string.leave_group,
                messageResId = R.string.select_another_admin_message
            )
        }
    }
}