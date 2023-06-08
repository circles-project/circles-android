package org.futo.circles.feature.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.extensions.isCurrentUserAbleToChangeSettings
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.extensions.isCurrentUserAbleToPost
import org.futo.circles.core.extensions.isCurrentUserOnlyAdmin
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.setToolbarSubTitle
import org.futo.circles.core.extensions.setToolbarTitle
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.share.ShareProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.databinding.FragmentTimelineBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.timeline.list.TimelineAdapter
import org.futo.circles.feature.timeline.poll.CreatePollListener
import org.futo.circles.feature.timeline.post.create.CreatePostListener
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.*
import org.futo.circles.view.CreatePostMenuListener
import org.futo.circles.view.PostOptionsListener
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

@AndroidEntryPoint
class TimelineFragment : Fragment(R.layout.fragment_timeline), PostOptionsListener,
    CreatePostListener, CreatePollListener, EmojiPickerListener, MenuProvider {

    private val args: TimelineFragmentArgs by navArgs()
    private val viewModel by viewModels<TimelineViewModel>()

    private val isGroupMode by lazy { args.type == CircleRoomTypeArg.Group }
    private val isThread by lazy { args.threadEventId != null }

    private val timelineId by lazy {
        if (isGroupMode) args.roomId
        else getTimelineRoomFor(args.roomId)?.roomId ?: throw IllegalArgumentException(
            requireContext().getString(R.string.timeline_not_found)
        )
    }
    private val binding by viewBinding(FragmentTimelineBinding::bind)
    private val listAdapter by lazy {
        TimelineAdapter(
            getCurrentUserPowerLevel(args.roomId),
            this,
            isThread
        ) { viewModel.loadMore() }
    }
    private val navigator by lazy { TimelineNavigator(this) }
    private var groupPowerLevelsContent: PowerLevelsContent? = null
    private var isNotificationsEnabledForRoom: Boolean = true
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onDetach() {
        setToolbarSubTitle("")
        super.onDetach()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        if (isThread) return
        if (isGroupMode) inflateGroupMenu(menu, inflater) else inflateCircleMenu(menu, inflater)
    }

    private fun inflateGroupMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_timeline_menu, menu)
        menu.findItem(R.id.configureGroup).isVisible =
            groupPowerLevelsContent?.isCurrentUserAbleToChangeSettings() ?: false
        menu.findItem(R.id.inviteMembers).isVisible =
            groupPowerLevelsContent?.isCurrentUserAbleToInvite() ?: false
        menu.findItem(R.id.deleteGroup).isVisible =
            groupPowerLevelsContent?.isCurrentUserOnlyAdmin(args.roomId) ?: false
        menu.findItem(R.id.stateEvents).isVisible =
            preferencesProvider.isDeveloperModeEnabled()
        menu.findItem(R.id.muteNotifications).isVisible = isNotificationsEnabledForRoom
        menu.findItem(R.id.unMuteNotifications).isVisible = !isNotificationsEnabledForRoom
    }

    private fun inflateCircleMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.circle_timeline_menu, menu)
        menu.findItem(R.id.stateEvents).isVisible = preferencesProvider.isDeveloperModeEnabled()
        menu.findItem(R.id.muteNotifications).isVisible = isNotificationsEnabledForRoom
        menu.findItem(R.id.unMuteNotifications).isVisible = !isNotificationsEnabledForRoom
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.muteNotifications ->
                viewModel.setNotificationsEnabled(false)

            R.id.unMuteNotifications ->
                viewModel.setNotificationsEnabled(true)

            R.id.configureGroup, R.id.configureCircle ->
                navigator.navigateToUpdateRoom(args.roomId, args.type)

            R.id.manageMembers, R.id.myFollowers ->
                navigator.navigateToManageMembers(timelineId, args.type)

            R.id.inviteMembers, R.id.inviteFollowers -> navigator.navigateToInviteMembers(timelineId)
            R.id.leaveGroup -> showLeaveGroupDialog()
            R.id.iFollowing -> navigator.navigateToFollowing(args.roomId)
            R.id.deleteCircle -> withConfirmation(DeleteCircle()) { viewModel.deleteCircle() }
            R.id.deleteGroup -> withConfirmation(DeleteGroup()) { viewModel.deleteGroup() }
            R.id.stateEvents -> navigator.navigateToStateEvents(timelineId)
        }
        return true
    }

    private fun setupViews() {
        binding.rvTimeline.apply {
            adapter = listAdapter
            itemAnimator = null
            MarkAsReadBuffer(this) { viewModel.markEventAsRead(it) }
        }
        binding.fabMenu.setUp(object : CreatePostMenuListener {
            override fun onCreatePoll() {
                navigator.navigateToCreatePoll(timelineId)
            }

            override fun onCreatePost() {
                navigator.navigateToCreatePost(timelineId)
            }
        }, binding.rvTimeline, isThread)
    }

    private fun setupObservers() {
        viewModel.titleLiveData?.observeData(this) { roomName ->
            val title = if (isThread) getString(R.string.thread_format, roomName ?: "")
            else roomName ?: ""
            setToolbarTitle(title)
        }
        viewModel.timelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
        viewModel.notificationsStateLiveData.observeData(this) {
            isNotificationsEnabledForRoom = it
            setToolbarSubTitle(if (it) "" else getString(R.string.notifications_disabled))
            activity?.invalidateOptionsMenu()
        }
        viewModel.accessLevelLiveData.observeData(this) { powerLevelsContent ->
            onUserAccessLevelChanged(powerLevelsContent)
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.saveToDeviceLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.saved), true) }
        }
        viewModel.ignoreUserLiveData.observeResponse(this,
            success = {
                context?.let { showSuccess(it.getString(R.string.user_ignored), true) }
            })
        viewModel.unSendReactionLiveData.observeResponse(this)
        viewModel.leaveGroupLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
        viewModel.deleteCircleLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    override fun onUserClicked(userId: String) {
        navigator.navigateToUserDialogFragment(userId)
    }

    override fun onShowPreview(roomId: String, eventId: String) {
        navigator.navigateToShowMediaPreview(roomId, eventId)
    }

    override fun onShowEmoji(roomId: String, eventId: String) {
        navigator.navigateToShowEmoji(roomId, eventId)
    }

    override fun onReply(roomId: String, eventId: String) {
        navigator.navigateToThread(roomId, eventId)
    }

    override fun onShare(content: PostContent) {
        viewModel.sharePostContent(content)
    }

    override fun onRemove(roomId: String, eventId: String) {
        withConfirmation(RemovePost()) { viewModel.removeMessage(roomId, eventId) }
    }

    override fun onIgnore(senderId: String) {
        withConfirmation(IgnoreSender()) { viewModel.ignoreSender(senderId) }
    }

    override fun onSaveToDevice(content: PostContent) {
        viewModel.saveToDevice(content)
    }

    override fun onEditPostClicked(roomId: String, eventId: String) {
        navigator.navigateToCreatePost(roomId, eventId = eventId, isEdit = true)
    }

    override fun onSaveToGallery(roomId: String, eventId: String) {
        navigator.navigateToSaveToGallery(roomId, eventId)
    }

    override fun onReport(roomId: String, eventId: String) {
        navigator.navigateToReport(roomId, eventId)
    }

    override fun onEmojiChipClicked(
        roomId: String, eventId: String, emoji: String, isUnSend: Boolean
    ) {
        if (viewModel.accessLevelLiveData.value?.isCurrentUserAbleToPost() != true) {
            showError(getString(R.string.you_can_not_post_to_this_room))
            return
        }
        if (isUnSend) viewModel.unSendReaction(roomId, eventId, emoji)
        else viewModel.sendReaction(roomId, eventId, emoji)
    }

    override fun onPollOptionSelected(roomId: String, eventId: String, optionId: String) {
        viewModel.pollVote(roomId, eventId, optionId)
    }

    override fun endPoll(roomId: String, eventId: String) {
        withConfirmation(EndPoll()) { viewModel.endPoll(roomId, eventId) }
    }

    override fun onEditPollClicked(roomId: String, eventId: String) {
        navigator.navigateToCreatePoll(roomId, eventId)
    }

    override fun onInfoClicked(roomId: String, eventId: String) {
        navigator.navigateToInfo(roomId, eventId)
    }

    override fun onSendPost(
        roomId: String,
        postContent: CreatePostContent,
        threadEventId: String?
    ) {
        viewModel.sendPost(roomId, postContent, args.threadEventId)
    }

    override fun onEditTextPost(roomId: String, newMessage: String, eventId: String) {
        viewModel.editTextPost(eventId, roomId, newMessage)
    }

    override fun onCreatePoll(roomId: String, pollContent: CreatePollContent) {
        viewModel.createPoll(roomId, pollContent)
    }

    override fun onEditPoll(roomId: String, eventId: String, pollContent: CreatePollContent) {
        viewModel.editPoll(roomId, eventId, pollContent)
    }

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        roomId ?: return
        eventId ?: return
        viewModel.sendReaction(roomId, eventId, emoji)
    }

    private fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        if (isGroupMode) onGroupUserAccessLevelChanged(powerLevelsContent)
        else onCircleUserAccessLeveChanged(powerLevelsContent)
        listAdapter.updateUserPowerLevel(getCurrentUserPowerLevel(args.roomId))
    }

    private fun onGroupUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.fabMenu.setIsVisible(powerLevelsContent.isCurrentUserAbleToPost())
        groupPowerLevelsContent = powerLevelsContent
        activity?.invalidateOptionsMenu()
    }

    private fun onCircleUserAccessLeveChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.fabMenu.setIsVisible(isUserAdmin)
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