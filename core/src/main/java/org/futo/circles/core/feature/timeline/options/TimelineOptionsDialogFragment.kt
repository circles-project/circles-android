package org.futo.circles.core.feature.timeline.options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentTimelineOptionsBinding
import org.futo.circles.core.extensions.isCurrentUserAbleToChangeSettings
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.extensions.isCurrentUserOnlyAdmin
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.toRoomInfo
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.DeleteCircle
import org.futo.circles.core.model.DeleteGallery
import org.futo.circles.core.model.DeleteGroup
import org.futo.circles.core.model.LeaveGallery
import org.futo.circles.core.model.LeaveGroup
import org.futo.circles.core.model.UnfollowTimeline
import org.futo.circles.core.provider.PreferencesProvider

@AndroidEntryPoint
class TimelineOptionsDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentTimelineOptionsBinding>(
        DialogFragmentTimelineOptionsBinding::inflate
    ) {

    private val args: TimelineOptionsDialogFragmentArgs by navArgs()

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
            lPushNotifications.apply {
                setOnClickListener { viewModel.setNotificationsEnabled(!svPushNotifications.isChecked) }
            }
            tvConfigure.apply {
                setText(
                    getString(
                        when (args.type) {
                            CircleRoomTypeArg.Circle -> R.string.configure_circle
                            CircleRoomTypeArg.Group -> R.string.configure_group
                            CircleRoomTypeArg.Photo -> R.string.configure_gallery
                        }
                    )
                )
                setOnClickListener { navigator.navigateToUpdateRoom(args.roomId, args.type) }
            }
            tvDelete.apply {
                text = getString(
                    when (args.type) {
                        CircleRoomTypeArg.Circle -> R.string.delete_circle
                        CircleRoomTypeArg.Group -> R.string.delete_group
                        CircleRoomTypeArg.Photo -> R.string.delete_gallery
                    }
                )
                setOnClickListener {
                    withConfirmation(
                        when (args.type) {
                            CircleRoomTypeArg.Circle -> DeleteCircle()
                            CircleRoomTypeArg.Group -> DeleteGroup()
                            CircleRoomTypeArg.Photo -> DeleteGallery()
                        }
                    ) {
                        viewModel.delete()
                    }
                }
            }
            tvStateEvents.apply {
                setIsVisible(preferencesProvider.isDeveloperModeEnabled())
                setOnClickListener { navigator.navigateToStateEvents(args.roomId) }
            }
            tvInviteMembers.apply {
                setText(
                    getString(
                        if (args.type == CircleRoomTypeArg.Circle) R.string.invite_followers
                        else R.string.invite_members
                    )
                )
                setOnClickListener { navigator.navigateToInviteMembers(args.roomId) }
            }
            tvKnockRequests.setOnClickListener {
                navigator.navigateToRequestForInvite(args.type, args.roomId)
            }
            tvLeave.apply {
                setText(
                    getString(
                        when (args.type) {
                            CircleRoomTypeArg.Circle -> R.string.unfollow_circle
                            CircleRoomTypeArg.Group -> R.string.leave_group
                            CircleRoomTypeArg.Photo -> R.string.leave_gallery
                        }
                    )
                )
                setOnClickListener { showLeaveRoomDialog() }
            }
            tvShare.setOnClickListener { navigator.navigateToShareRoom(args.roomId, args.type) }
            tvManageMembers.apply {
                setText(getString(if (args.type == CircleRoomTypeArg.Circle) R.string.followers else R.string.manage_members))
                setOnClickListener { navigator.navigateToManageMembers(args.roomId, args.type) }
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
            with(binding) {
                tvConfigure.setIsVisible(groupPowerLevelsContent.isCurrentUserAbleToChangeSettings())
                tvInviteMembers.setIsVisible(groupPowerLevelsContent.isCurrentUserAbleToInvite())
                tvKnockRequests.setIsVisible(groupPowerLevelsContent.isCurrentUserAbleToInvite())
                tvDelete.setIsVisible(groupPowerLevelsContent.isCurrentUserOnlyAdmin(args.roomId))
            }
        }
        viewModel.roomSummaryLiveData?.observeData(this) {
            it.getOrNull()?.let { room ->
                val roomInfo = room.toRoomInfo()
                binding.ivCover.loadRoomProfileIcon(
                    roomInfo.avatarUrl,
                    roomInfo.title
                )
                binding.toolbar.title = roomInfo.title
            }
        }
        viewModel.notificationsStateLiveData.observeData(this) {
            binding.svPushNotifications.isChecked = it
        }
        viewModel.knockRequestCountLiveData.observeData(this) {
            binding.ivKnocksCount.apply {
                setIsVisible(it > 0)
                setCount(it)
            }
        }
    }


    private fun showLeaveRoomDialog() {
        if (viewModel.canLeaveRoom()) {
            withConfirmation(
                when (args.type) {
                    CircleRoomTypeArg.Circle -> UnfollowTimeline()
                    CircleRoomTypeArg.Group -> LeaveGroup()
                    CircleRoomTypeArg.Photo -> LeaveGallery()
                }
            ) { viewModel.leaveRoom() }
        } else {
            showDialog(
                titleResIdRes = R.string.leave_room,
                messageResId = R.string.select_another_admin_message
            )
        }
    }
}