package org.futo.circles.feature.timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
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
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.share.ShareProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.databinding.DialogFragmentTimelineBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.timeline.list.TimelineAdapter
import org.futo.circles.feature.timeline.poll.CreatePollListener
import org.futo.circles.feature.timeline.post.create.CreatePostListener
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.*
import org.futo.circles.view.CreatePostViewListener
import org.futo.circles.view.PostOptionsListener
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

@AndroidEntryPoint
class TimelineDialogFragment : BaseFullscreenDialogFragment(DialogFragmentTimelineBinding::inflate),
    PostOptionsListener,
    CreatePostListener, CreatePollListener, EmojiPickerListener {

    private val args: TimelineDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<TimelineViewModel>()

    private val isGroupMode by lazy { args.type == CircleRoomTypeArg.Group }
    private val isThread by lazy { args.threadEventId != null }

    private val timelineId by lazy {
        if (isGroupMode) args.roomId
        else getTimelineRoomFor(args.roomId)?.roomId ?: throw IllegalArgumentException(
            requireContext().getString(R.string.timeline_not_found)
        )
    }
    private val binding by lazy {
        getBinding() as DialogFragmentTimelineBinding
    }
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
        setupMenu()
    }

    @SuppressLint("RestrictedApi")
    private fun setupMenu() {
        with(binding.toolbar) {
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            if (isThread) return
            if (isGroupMode) inflateGroupMenu(menu) else inflateCircleMenu(menu)
            setupMenuClickListener()
        }
    }

    private fun invalidateMenu(){
        binding.toolbar.menu.clear()
        setupMenu()
    }

    private fun inflateGroupMenu(menu: Menu) {
        binding.toolbar.inflateMenu(R.menu.group_timeline_menu)
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

    private fun inflateCircleMenu(menu: Menu) {
        binding.toolbar.inflateMenu(R.menu.circle_timeline_menu)
        menu.findItem(R.id.stateEvents).isVisible = preferencesProvider.isDeveloperModeEnabled()
        menu.findItem(R.id.muteNotifications).isVisible = isNotificationsEnabledForRoom
        menu.findItem(R.id.unMuteNotifications).isVisible = !isNotificationsEnabledForRoom
    }

    private fun setupMenuClickListener() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.muteNotifications ->
                    viewModel.setNotificationsEnabled(false)

                R.id.unMuteNotifications ->
                    viewModel.setNotificationsEnabled(true)

                R.id.configureGroup, R.id.configureCircle ->
                    navigator.navigateToUpdateRoom(args.roomId, args.type)

                R.id.manageMembers, R.id.myFollowers ->
                    navigator.navigateToManageMembers(timelineId, args.type)

                R.id.inviteMembers, R.id.inviteFollowers -> navigator.navigateToInviteMembers(
                    timelineId
                )

                R.id.leaveGroup -> showLeaveGroupDialog()
                R.id.iFollowing -> navigator.navigateToFollowing(args.roomId)
                R.id.deleteCircle -> withConfirmation(DeleteCircle()) { viewModel.deleteCircle() }
                R.id.deleteGroup -> withConfirmation(DeleteGroup()) { viewModel.deleteGroup() }
                R.id.stateEvents -> navigator.navigateToStateEvents(timelineId)
                R.id.share -> navigator.navigateToShareRoom(timelineId)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun setupViews() {
        binding.rvTimeline.apply {
            adapter = listAdapter
            MarkAsReadBuffer(this) { viewModel.markEventAsRead(it) }
        }
        binding.lCreatePost.setUp(object : CreatePostViewListener {
            override fun onCreatePoll() {
                navigator.navigateToCreatePoll(timelineId)
            }

            override fun onCreatePost() {
                navigator.navigateToCreatePost(timelineId, args.threadEventId)
            }
        }, binding.rvTimeline, isThread)
    }

    private fun setupObservers() {
        viewModel.titleLiveData?.observeData(this) { roomName ->
            val title = if (isThread) getString(R.string.thread_format, roomName ?: "")
            else roomName ?: ""
            binding.toolbar.title = title
        }
        viewModel.timelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
        viewModel.notificationsStateLiveData.observeData(this) {
            isNotificationsEnabledForRoom = it
            binding.toolbar.subtitle = if (it) "" else getString(R.string.notifications_disabled)
            invalidateMenu()
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
        viewModel.profileLiveData?.observeData(this) {
            it.getOrNull()?.let { binding.lCreatePost.setUserInfo(it) }
        }
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
        viewModel.sendPost(roomId, postContent, threadEventId)
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
        binding.lCreatePost.setIsVisible(powerLevelsContent.isCurrentUserAbleToPost())
        groupPowerLevelsContent = powerLevelsContent
        invalidateMenu()
    }

    private fun onCircleUserAccessLeveChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.lCreatePost.setIsVisible(isUserAdmin)
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