package com.futo.circles.feature.timeline

import android.annotation.SuppressLint
import android.net.Uri
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
import com.futo.circles.databinding.TimelineFragmentBinding
import com.futo.circles.extensions.*
import com.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import com.futo.circles.feature.timeline.post.CreatePostListener
import com.futo.circles.feature.timeline.post.share.ShareProvider
import com.futo.circles.feature.timeline.list.PostViewHolder
import com.futo.circles.feature.timeline.list.TimelineAdapter
import com.futo.circles.model.CircleRoomTypeArg
import com.futo.circles.model.ImageContent
import com.futo.circles.model.PostContent
import com.futo.circles.view.PostOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class TimelineFragment : Fragment(R.layout.timeline_fragment), PostOptionsListener,
    CreatePostListener, EmojiPickerListener {

    private val args: TimelineFragmentArgs by navArgs()
    private val viewModel by viewModel<TimelineViewModel> { parametersOf(args.roomId, args.type) }
    private val isGroupMode by lazy { args.type == CircleRoomTypeArg.Group }

    private val timelineId by lazy {
        if (isGroupMode) args.roomId
        else getTimelineRoomFor(args.roomId)?.roomId ?: throw IllegalArgumentException(
            requireContext().getString(R.string.timeline_not_found)
        )
    }
    private val binding by viewBinding(TimelineFragmentBinding::bind)
    private val listAdapter by lazy {
        TimelineAdapter(getCurrentUserPowerLevel(args.roomId), this) { viewModel.loadMore() }
    }
    private var isGroupSettingAvailable = false
    private var isGroupInviteAvailable = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupViews()
        setupObservers()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        if (isGroupMode) inflateGroupMenu(menu, inflater) else inflateCircleMenu(menu, inflater)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun inflateGroupMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_timeline_menu, menu)
        menu.findItem(R.id.configureGroup).isVisible = isGroupSettingAvailable
        menu.findItem(R.id.inviteMembers).isVisible = isGroupInviteAvailable
    }

    private fun inflateCircleMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.circle_timeline_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureGroup, R.id.configureCircle -> {
                navigateToUpdateRoom()
                return true
            }
            R.id.manageMembers, R.id.myFollowers -> {
                navigateToManageMembers()
                return true
            }
            R.id.inviteMembers, R.id.inviteFollowers -> {
                navigateToInviteMembers()
                return true
            }
            R.id.leaveGroup -> {
                showLeaveGroupDialog()
                return true
            }
            R.id.iFollowing -> {
                navigateToFollowing()
                return true
            }
            R.id.deleteCircle -> {
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
        binding.rvTimeline.apply {
            adapter = listAdapter
            addItemDecoration(
                BaseRvDecoration.OffsetDecoration<PostViewHolder>(offset = context.dimen(R.dimen.group_post_item_offset))
            )
            bindToFab(binding.fbCreatePost)
        }
        binding.fbCreatePost.setOnClickListener { navigateToCreatePost(timelineId) }
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
        viewModel.downloadImageLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.image_saved), true) }
        }
        viewModel.ignoreUserLiveData.observeResponse(this,
            success = {
                context?.let { showSuccess(it.getString(R.string.user_ignored), true) }
            })
        viewModel.unSendReactionLiveData.observeResponse(this)
        viewModel.leaveGroupLiveData.observeResponse(this,
            success = { activity?.onBackPressed() }
        )
        viewModel.deleteCircleLiveData.observeResponse(this,
            success = { activity?.onBackPressed() }
        )
    }

    override fun onShowRepliesClicked(eventId: String) {
        viewModel.toggleRepliesVisibilityFor(eventId)
    }

    override fun onShowEmoji(roomId: String, eventId: String) {
        navigateToEmojiPicker(roomId, eventId)
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

    override fun onSaveImage(imageContent: ImageContent) {
        viewModel.saveImage(imageContent)
    }

    override fun onReport(roomId: String, eventId: String) {
        navigateToReport(roomId, eventId)
    }

    override fun onEmojiChipClicked(
        roomId: String, eventId: String, emoji: String, isUnSend: Boolean
    ) {
        if (isUnSend) viewModel.unSendReaction(roomId, eventId, emoji)
        else viewModel.sendReaction(roomId, eventId, emoji)
    }

    override fun onSendTextPost(roomId: String, message: String, threadEventId: String?) {
        viewModel.sendTextPost(roomId, message, threadEventId)
    }

    override fun onSendImagePost(roomId: String, uri: Uri, threadEventId: String?) {
        viewModel.sendImagePost(roomId, uri, threadEventId)
    }

    override fun onEmojiSelected(roomId: String, eventId: String, emoji: String) {
        viewModel.sendReaction(roomId, eventId, emoji)
    }

    private fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        if (isGroupMode) onGroupUserAccessLevelChanged(powerLevelsContent)
        else onCircleUserAccessLeveChanged(powerLevelsContent)
    }

    private fun onGroupUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.fbCreatePost.setIsVisible(powerLevelsContent.isCurrentUserAbleToPost())
        isGroupSettingAvailable = powerLevelsContent.isCurrentUserAbleToChangeSettings()
        isGroupInviteAvailable = powerLevelsContent.isCurrentUserAbleToInvite()
        activity?.invalidateOptionsMenu()
    }

    private fun onCircleUserAccessLeveChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.fbCreatePost.setIsVisible(isUserAdmin)
    }

    private fun navigateToCreatePost(
        roomId: String,
        userName: String? = null,
        eventId: String? = null
    ) {
        findNavController().navigate(
            TimelineFragmentDirections.toCreatePostBottomSheet(roomId, userName, eventId)
        )
    }

    private fun navigateToEmojiPicker(roomId: String, eventId: String) {
        findNavController().navigate(
            TimelineFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    private fun navigateToReport(roomId: String, eventId: String) {
        findNavController().navigate(
            TimelineFragmentDirections.toReportDialogFragment(roomId, eventId)
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
        if (viewModel.isSingleOwner()) {
            showDialog(
                titleResIdRes = R.string.leave_group,
                messageResId = R.string.select_another_admin_message
            )
        } else {
            showDialog(
                titleResIdRes = R.string.leave_group,
                messageResId = R.string.leave_group_message,
                positiveButtonRes = R.string.leave,
                negativeButtonVisible = true,
                positiveAction = { viewModel.leaveGroup() }
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