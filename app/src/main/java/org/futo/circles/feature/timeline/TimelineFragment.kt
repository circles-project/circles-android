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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.databinding.FragmentTimelineBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.share.ShareProvider
import org.futo.circles.feature.timeline.list.PostViewHolder
import org.futo.circles.feature.timeline.list.TimelineAdapter
import org.futo.circles.feature.timeline.poll.CreatePollListener
import org.futo.circles.feature.timeline.post.create.CreatePostListener
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.CreatePollContent
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.PostContent
import org.futo.circles.view.CreatePostMenuListener
import org.futo.circles.view.PostOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class TimelineFragment : Fragment(R.layout.fragment_timeline), PostOptionsListener,
    CreatePostListener, CreatePollListener, EmojiPickerListener, MenuProvider {

    private val args: TimelineFragmentArgs by navArgs()
    private val viewModel by viewModel<TimelineViewModel> { parametersOf(args.roomId, args.type) }
    private val isGroupMode by lazy { args.type == CircleRoomTypeArg.Group }

    private val timelineId by lazy {
        if (isGroupMode) args.roomId
        else getTimelineRoomFor(args.roomId)?.roomId ?: throw IllegalArgumentException(
            requireContext().getString(R.string.timeline_not_found)
        )
    }
    private val binding by viewBinding(FragmentTimelineBinding::bind)
    private val listAdapter by lazy {
        TimelineAdapter(getCurrentUserPowerLevel(args.roomId), this) { viewModel.loadMore() }
    }
    private var isGroupSettingAvailable = false
    private var isGroupInviteAvailable = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        if (isGroupMode) inflateGroupMenu(menu, inflater) else inflateCircleMenu(menu, inflater)
    }

    private fun inflateGroupMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_timeline_menu, menu)
        menu.findItem(R.id.configureGroup).isVisible = isGroupSettingAvailable
        menu.findItem(R.id.inviteMembers).isVisible = isGroupInviteAvailable
    }

    private fun inflateCircleMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.circle_timeline_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureGroup, R.id.configureCircle -> navigateToUpdateRoom()
            R.id.manageMembers, R.id.myFollowers -> navigateToManageMembers()
            R.id.inviteMembers, R.id.inviteFollowers -> navigateToInviteMembers()
            R.id.leaveGroup -> showLeaveGroupDialog()
            R.id.iFollowing -> navigateToFollowing()
            R.id.deleteCircle -> showDeleteConfirmation()
        }
        return true
    }

    private fun setupViews() {
        binding.rvTimeline.apply {
            adapter = listAdapter
            itemAnimator = null
            addItemDecoration(
                BaseRvDecoration.OffsetDecoration<PostViewHolder>(offset = context.dimen(R.dimen.group_post_item_offset))
            )
        }
        binding.fabMenu.apply {
            bindToRecyclerView(binding.rvTimeline)
            setListener(object : CreatePostMenuListener {
                override fun onCreatePoll() {
                    navigateToCreatePoll(timelineId)
                }

                override fun onCreatePost() {
                    navigateToCreatePost(timelineId)
                }
            })
        }
    }

    private fun setupObservers() {
        viewModel.titleLiveData?.observeData(this) { title ->
            setToolbarTitle(title ?: "")
        }
        viewModel.timelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
        viewModel.accessLevelLiveData.observeData(this) { powerLevelsContent ->
            onUserAccessLevelChanged(powerLevelsContent)
        }
        viewModel.scrollToTopLiveData.observeData(this) {
            binding.rvTimeline.postDelayed(
                { binding.rvTimeline.scrollToPosition(0) }, 500
            )
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

    override fun onShowRepliesClicked(eventId: String) {
        viewModel.toggleRepliesVisibilityFor(eventId)
    }

    override fun onShowPreview(roomId: String, eventId: String) {
        findNavController().navigate(
            TimelineFragmentDirections.toMediaPreviewDialogFragment(roomId, eventId)
        )
    }

    override fun onShowEmoji(roomId: String, eventId: String) {
        findNavController().navigate(
            TimelineFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    override fun onReply(roomId: String, eventId: String, userName: String) {
        navigateToCreatePost(roomId, userName, eventId)
    }

    override fun onShare(content: PostContent) {
        viewModel.sharePostContent(content)
    }

    override fun onRemove(roomId: String, eventId: String) {
        showDialog(
            titleResIdRes = R.string.remove_post,
            messageResId = R.string.remove_post_message,
            positiveButtonRes = R.string.remove,
            negativeButtonVisible = true,
            positiveAction = { viewModel.removeMessage(roomId, eventId) }
        )
    }

    override fun onIgnore(senderId: String) {
        showDialog(
            titleResIdRes = R.string.ignore_sender,
            messageResId = R.string.ignore_user_message,
            positiveButtonRes = R.string.ignore,
            negativeButtonVisible = true,
            positiveAction = { viewModel.ignoreSender(senderId) }
        )
    }

    override fun onSaveToDevice(content: PostContent) {
        viewModel.saveToDevice(content)
    }

    override fun onEditPostClicked(roomId: String, eventId: String) {
        navigateToCreatePost(roomId, eventId = eventId, isEdit = true)
    }

    override fun onSaveToGallery(roomId: String, eventId: String) {
        findNavController().navigate(
            TimelineFragmentDirections.toSaveToGalleyDialogFragment(roomId, eventId)
        )
    }

    override fun onReport(roomId: String, eventId: String) {
        findNavController().navigate(
            TimelineFragmentDirections.toReportDialogFragment(roomId, eventId)
        )
    }

    override fun onEmojiChipClicked(
        roomId: String, eventId: String, emoji: String, isUnSend: Boolean
    ) {
        if (isUnSend) viewModel.unSendReaction(roomId, eventId, emoji)
        else viewModel.sendReaction(roomId, eventId, emoji)
    }

    override fun onPollOptionSelected(roomId: String, eventId: String, optionId: String) {
        viewModel.pollVote(roomId, eventId, optionId)
    }

    override fun endPoll(roomId: String, eventId: String) {
        showDialog(
            titleResIdRes = R.string.end_poll,
            messageResId = R.string.end_poll_message,
            positiveButtonRes = R.string.end_poll,
            negativeButtonVisible = true,
            positiveAction = { viewModel.endPoll(roomId, eventId) }
        )
    }

    override fun onEditPollClicked(roomId: String, eventId: String) {
        navigateToCreatePoll(roomId, eventId)
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

    override fun onEmojiSelected(roomId: String, eventId: String, emoji: String) {
        viewModel.sendReaction(roomId, eventId, emoji)
    }

    private fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        if (isGroupMode) onGroupUserAccessLevelChanged(powerLevelsContent)
        else onCircleUserAccessLeveChanged(powerLevelsContent)
    }

    private fun onGroupUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.fabMenu.setIsVisible(powerLevelsContent.isCurrentUserAbleToPost())
        isGroupSettingAvailable = powerLevelsContent.isCurrentUserAbleToChangeSettings()
        isGroupInviteAvailable = powerLevelsContent.isCurrentUserAbleToInvite()
        activity?.invalidateOptionsMenu()
    }

    private fun onCircleUserAccessLeveChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.fabMenu.setIsVisible(isUserAdmin)
    }

    private fun navigateToCreatePost(
        roomId: String,
        userName: String? = null,
        eventId: String? = null,
        isEdit: Boolean = false
    ) {
        findNavController().navigate(
            TimelineFragmentDirections.toCreatePostBottomSheet(roomId, userName, eventId, isEdit)
        )
    }

    private fun navigateToCreatePoll(roomId: String, eventId: String? = null) {
        findNavController().navigate(
            TimelineFragmentDirections.toCreatePoll(roomId, eventId)
        )
    }

    private fun navigateToInviteMembers() {
        findNavController().navigate(
            TimelineFragmentDirections.toInviteMembersDialogFragment(timelineId)
        )
    }

    private fun navigateToUpdateRoom() {
        findNavController().navigate(
            TimelineFragmentDirections.toUpdateRoomDialogFragment(args.roomId, args.type)
        )
    }

    private fun navigateToManageMembers() {
        findNavController().navigate(
            TimelineFragmentDirections.toManageMembersDialogFragment(timelineId, args.type)
        )
    }

    private fun navigateToFollowing() {
        findNavController().navigate(
            TimelineFragmentDirections.toFollowingDialogFragment(args.roomId)
        )
    }

    private fun showLeaveGroupDialog() {
        if (viewModel.canLeaveRoom()) {
            showDialog(
                titleResIdRes = R.string.leave_group,
                messageResId = R.string.leave_group_message,
                positiveButtonRes = R.string.leave,
                negativeButtonVisible = true,
                positiveAction = { viewModel.leaveGroup() }
            )
        } else {
            showDialog(
                titleResIdRes = R.string.leave_group,
                messageResId = R.string.select_another_admin_message
            )
        }
    }

    private fun showDeleteConfirmation() {
        showDialog(
            titleResIdRes = R.string.delete_circle,
            messageResId = R.string.delete_circle_message,
            positiveButtonRes = R.string.delete,
            negativeButtonVisible = true,
            positiveAction = { viewModel.deleteCircle() }
        )
    }
}