package org.futo.circles.feature.timeline.thread

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentThreadTimelineBinding
import org.futo.circles.extensions.bindToFab
import org.futo.circles.extensions.getCurrentUserPowerLevel
import org.futo.circles.extensions.isCurrentUserAbleToPost
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.extensions.showError
import org.futo.circles.extensions.showSuccess
import org.futo.circles.extensions.withConfirmation
import org.futo.circles.feature.share.ShareProvider
import org.futo.circles.feature.timeline.list.TimelineAdapter
import org.futo.circles.feature.timeline.post.create.CreatePostListener
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.ConfirmationType
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.PostContent
import org.futo.circles.view.PostOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

class ThreadTimelineDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentThreadTimelineBinding::inflate), PostOptionsListener,
    CreatePostListener, EmojiPickerListener {

    private val args: ThreadTimelineDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ThreadTimelineViewModel> {
        parametersOf(args.roomId, args.eventId)
    }

    private val navigator by lazy { ThreadTimelineNavigator(this) }

    private val binding by lazy {
        getBinding() as DialogFragmentThreadTimelineBinding
    }

    private val listAdapter by lazy {
        TimelineAdapter(getCurrentUserPowerLevel(args.roomId), this) {
            // viewModel.loadMore()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvThreadTimeline.apply {
            adapter = listAdapter
            itemAnimator = null
            bindToFab(binding.fbCreatePost)
            // MarkAsReadBuffer(this) { viewModel.markEventAsRead(it) }
        }
        binding.fbCreatePost.setOnClickListener {
            //navigator.navigateToCreatePost(timelineId)
        }
    }

    private fun setupObservers() {
//        viewModel.timelineEventsLiveData.observeData(this) {
//            listAdapter.submitList(it)
//        }
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
    }

    override fun onUserClicked(userId: String) {
        navigator.navigateToUserDialogFragment(userId)
    }

    override fun onShowRepliesClicked(eventId: String) {
        //viewModel.toggleRepliesVisibilityFor(eventId)
    }

    override fun onShowPreview(roomId: String, eventId: String) {
        navigator.navigateToShowMediaPreview(roomId, eventId)
    }

    override fun onShowEmoji(roomId: String, eventId: String) {
        navigator.navigateToShowEmoji(roomId, eventId)
    }

    override fun onReply(roomId: String, eventId: String, userName: String) {
        navigator.navigateToCreatePost(roomId, userName, eventId)
    }

    override fun onShare(content: PostContent) {
        viewModel.sharePostContent(content)
    }

    override fun onRemove(roomId: String, eventId: String) {
        withConfirmation(ConfirmationType.REMOVE_POST) { viewModel.removeMessage(roomId, eventId) }
    }

    override fun onIgnore(senderId: String) {
        withConfirmation(ConfirmationType.IGNORE_SENDER) { viewModel.ignoreSender(senderId) }
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

    }

    override fun endPoll(roomId: String, eventId: String) {

    }

    override fun onEditPollClicked(roomId: String, eventId: String) {

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

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        roomId ?: return
        eventId ?: return
        viewModel.sendReaction(roomId, eventId, emoji)
    }

    private fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.fbCreatePost.setIsVisible(powerLevelsContent.isCurrentUserAbleToPost())
        listAdapter.updateUserPowerLevel(getCurrentUserPowerLevel(args.roomId))
    }

}