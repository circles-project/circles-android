package com.futo.circles.feature.timeline

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.list.BaseRvDecoration
import com.futo.circles.feature.timeline.list.PostViewHolder
import com.futo.circles.feature.timeline.list.TimelineAdapter
import com.futo.circles.databinding.TimelineFragmentBinding
import com.futo.circles.extensions.*
import com.futo.circles.feature.emoji.EmojiPickerListener
import com.futo.circles.feature.post.CreatePostListener
import com.futo.circles.feature.share.ShareProvider
import com.futo.circles.model.ImageContent
import com.futo.circles.model.PostContent
import com.futo.circles.view.PostOptionsListener
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

abstract class BaseTimelineFragment : Fragment(R.layout.timeline_fragment), PostOptionsListener,
    CreatePostListener, EmojiPickerListener {

    abstract val viewModel: BaseTimelineViewModel
    abstract val roomId: String
    abstract val timelineId: String
    protected val binding by viewBinding(TimelineFragmentBinding::bind)
    private val listAdapter by lazy {
        TimelineAdapter(getCurrentUserPowerLevel(roomId), this) { viewModel.loadMore() }
    }

    abstract fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent)
    abstract fun navigateToCreatePost(
        roomId: String, userName: String? = null, eventId: String? = null
    )

    abstract fun navigateToEmojiPicker(roomId: String, eventId: String)
    abstract fun navigateToReport(roomId: String, eventId: String)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupViews()
        setupObservers()
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

    protected open fun setupObservers() {
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
}