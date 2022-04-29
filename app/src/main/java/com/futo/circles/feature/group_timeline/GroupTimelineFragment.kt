package com.futo.circles.feature.group_timeline

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
import com.futo.circles.databinding.GroupTimelineFragmentBinding
import com.futo.circles.extensions.*
import com.futo.circles.feature.emoji.EmojiPickerListener
import com.futo.circles.feature.group_timeline.list.GroupPostViewHolder
import com.futo.circles.feature.group_timeline.list.GroupTimelineAdapter
import com.futo.circles.feature.post.CreatePostListener
import com.futo.circles.feature.share.ShareProvider
import com.futo.circles.model.ImageContent
import com.futo.circles.model.Post
import com.futo.circles.model.PostContent
import com.futo.circles.view.GroupPostListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent


class GroupTimelineFragment : Fragment(R.layout.group_timeline_fragment), GroupPostListener,
    CreatePostListener, EmojiPickerListener {

    private val args: GroupTimelineFragmentArgs by navArgs()
    private val viewModel by viewModel<GroupTimelineViewModel> { parametersOf(args.roomId) }
    private val binding by viewBinding(GroupTimelineFragmentBinding::bind)
    private var isSettingAvailable = false
    private var isInviteAvailable = false

    private val listAdapter by lazy {
        GroupTimelineAdapter(getCurrentUserPowerLevel(args.roomId), this) { viewModel.loadMore() }
    }

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

    private fun setupViews() {
        binding.rvGroupTimeline.apply {
            adapter = listAdapter
            addItemDecoration(
                BaseRvDecoration.OffsetDecoration<GroupPostViewHolder>(
                    offset = context.dimen(R.dimen.group_post_item_offset)
                )
            )
            bindToFab(binding.fbCreatePost)
        }
        binding.fbCreatePost.setOnClickListener { navigateToCreatePost() }
    }

    private fun setupObservers() {
        with(viewModel) {
            titleLiveData?.observeData(this@GroupTimelineFragment) { title ->
                setToolbarTitle(title ?: "")
            }
            timelineEventsLiveData.observeData(this@GroupTimelineFragment, ::setTimelineList)
            leaveGroupLiveData.observeResponse(this@GroupTimelineFragment,
                success = { activity?.onBackPressed() }
            )
            accessLevelLiveData.observeData(this@GroupTimelineFragment) { powerContent ->
                handleAccessActionsVisibility(powerContent)
            }
            scrollToTopLiveData.observeData(this@GroupTimelineFragment) {
                binding.rvGroupTimeline.postDelayed(
                    { binding.rvGroupTimeline.scrollToPosition(0) }, 500
                )
            }
            shareLiveData.observeData(this@GroupTimelineFragment) { content ->
                context?.let { ShareProvider.share(it, content) }
            }
            downloadImageLiveData.observeData(this@GroupTimelineFragment) {
                context?.let { showSuccess(it.getString(R.string.image_saved), true) }
            }
            ignoreUserLiveData.observeResponse(this@GroupTimelineFragment,
                success = {
                    context?.let { showSuccess(it.getString(R.string.user_ignored), true) }
                })
            unSendReactionLiveData.observeResponse(this@GroupTimelineFragment)
        }
    }

    private fun setTimelineList(list: List<Post>) {
        listAdapter.submitList(list)
    }

    override fun onShowRepliesClicked(eventId: String) {
        viewModel.toggleRepliesVisibilityFor(eventId)
    }

    override fun onShowEmoji(eventId: String) {
        findNavController().navigate(GroupTimelineFragmentDirections.toEmojiBottomSheet(eventId))
    }

    override fun onReply(eventId: String, userName: String) {
        navigateToCreatePost(userName, eventId)
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
        binding.fbCreatePost.setIsVisible(powerContent.isCurrentUserAbleToPost())
        isSettingAvailable = powerContent.isCurrentUserAbleToChangeSettings()
        isInviteAvailable = powerContent.isCurrentUserAbleToInvite()
        activity?.invalidateOptionsMenu()
    }

    private fun navigateToCreatePost(userName: String? = null, eventId: String? = null) {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toCreatePostBottomSheet(userName, eventId)
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
            GroupTimelineFragmentDirections.toConfigureGroupDialogFragment(args.roomId)
        )
    }

    override fun onSendTextPost(message: String, threadEventId: String?) {
        viewModel.sendTextPost(message, threadEventId)
    }

    override fun onSendImagePost(uri: Uri, threadEventId: String?) {
        viewModel.sendImagePost(uri, threadEventId)
    }

    override fun onShare(content: PostContent) {
        viewModel.sharePostContent(content)
    }

    override fun onRemove(eventId: String) {
        showDialog(
            titleResIdRes = R.string.remove_post,
            messageResId = R.string.remove_post_message,
            positiveButtonRes = R.string.remove,
            negativeButtonVisible = true,
            positiveAction = { viewModel.removeMessage(eventId) }
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

    override fun onReport(eventId: String) {
        findNavController().navigate(
            GroupTimelineFragmentDirections.toReportDialogFragment(args.roomId, eventId)
        )
    }

    override fun onEmojiChipClicked(eventId: String, emoji: String, isUnSend: Boolean) {
        if (isUnSend) viewModel.unSendReaction(eventId, emoji)
        else viewModel.sendReaction(eventId, emoji)
    }

    override fun onEmojiSelected(eventId: String, emoji: String) {
        viewModel.sendReaction(eventId, emoji)
    }
}