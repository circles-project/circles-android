package org.futo.circles.feature.timeline

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.extensions.isCurrentUserAbleToPost
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.share.ShareProvider
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.databinding.DialogFragmentTimelineBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.feature.timeline.list.TimelineAdapter
import org.futo.circles.feature.timeline.poll.CreatePollListener
import org.futo.circles.feature.timeline.post.create.CreatePostListener
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.feature.timeline.post.menu.PostMenuListener
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.EndPoll
import org.futo.circles.model.IgnoreSender
import org.futo.circles.model.RemovePost
import org.futo.circles.view.CreatePostViewListener
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

@ExperimentalBadgeUtils
@AndroidEntryPoint
class TimelineDialogFragment : BaseFullscreenDialogFragment(DialogFragmentTimelineBinding::inflate),
    PostOptionsListener, PostMenuListener,
    CreatePostListener, CreatePollListener, EmojiPickerListener {

    private val args: TimelineDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<TimelineViewModel>()

    private val isGroupMode by lazy { args.timelineId == null }
    private val isThread by lazy { args.threadEventId != null }

    private val binding by lazy {
        getBinding() as DialogFragmentTimelineBinding
    }

    private val videoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    private val listAdapter by lazy {
        TimelineAdapter(this, isThread, videoPlayer).apply {
            setHasStableIds(true)
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    scrollToTopIfMyNewPostAdded(positionStart, itemCount)
                }
            })
        }
    }

    private val navigator by lazy { TimelineNavigator(this) }
    private val knocksCountBadgeDrawable by lazy {
        BadgeDrawable.create(requireContext()).apply {
            isVisible = false
            backgroundColor =
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
        }
    }

    private var onLocalAddEmojiCallback: ((String) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        setupMenu()
        stopVideoOnNewScreenOpen()
        binding.rvTimeline.getRecyclerView()
            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.loadMore()
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        listAdapter.stopVideoPlayback()
    }

    override fun onDestroy() {
        videoPlayer.stop()
        videoPlayer.release()
        super.onDestroy()
    }


    private fun setupViews() {
        binding.rvTimeline.apply {
            adapter = listAdapter
            getRecyclerView().isNestedScrollingEnabled = false
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        binding.lCreatePost.setUp(object : CreatePostViewListener {
            override fun onCreatePoll() {
                navigator.navigateToCreatePoll(args.timelineId ?: args.roomId)
            }

            override fun onCreatePost() {
                navigator.navigateToCreatePost(args.timelineId ?: args.roomId, args.threadEventId)
            }
        }, binding.rvTimeline.getRecyclerView(), isThread)

        if (!isThread) {
            BadgeUtils.attachBadgeDrawable(
                knocksCountBadgeDrawable, binding.toolbar,
                org.futo.circles.core.R.id.settings
            )
        }
    }


    private fun setupMenu() {
        with(binding.toolbar) {
            if (isThread) return
            inflateMenu(org.futo.circles.core.R.menu.timeline_menu)
            setupMenuClickListener()
        }
    }

    private fun setupMenuClickListener() {
        binding.toolbar.apply {
            setOnClickListener { navigateToTimelineOptions() }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    org.futo.circles.core.R.id.settings -> navigateToTimelineOptions()
                    org.futo.circles.core.R.id.filter -> navigator.navigateToTimelinesFilter(args.roomId)
                }
                return@setOnMenuItemClickListener true
            }
        }
    }


    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it) }
        viewModel.titleLiveData.observeData(this) { roomName ->
            val title = if (isThread) getString(R.string.thread_format, roomName) else roomName
            binding.toolbar.title = title
        }
        viewModel.timelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
            viewModel.markTimelineAsRead(args.roomId, isGroupMode)
        }
        viewModel.isFilterActiveLiveData.observeData(this) {
            val menuItem =
                tryOrNull { binding.toolbar.menu.findItem(org.futo.circles.core.R.id.filter) }
            menuItem?.isVisible = it
        }
        viewModel.notificationsStateLiveData.observeData(this) {
            binding.toolbar.subtitle =
                if (it) "" else getString(R.string.notifications_disabled)
        }
        viewModel.accessLevelLiveData.observeData(this) { powerLevelsContent ->
            onUserAccessLevelChanged(powerLevelsContent)
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.saveToDeviceLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.saved)) }
        }
        viewModel.ignoreUserLiveData.observeResponse(this,
            success = {
                context?.let { showSuccess(it.getString(org.futo.circles.core.R.string.user_ignored)) }
            })
        viewModel.unSendReactionLiveData.observeResponse(this)

        viewModel.profileLiveData?.observeData(this) {
            it.getOrNull()?.let { binding.lCreatePost.setUserInfo(it) }
        }
        viewModel.knockRequestCountLiveData.observeData(this) {
            knocksCountBadgeDrawable.apply {
                number = it
                isVisible = it > 0
            }
        }
    }

    override fun onShowMenuClicked(roomId: String, eventId: String) {
        if (showNoInternetConnection()) return
        navigator.navigatePostMenu(roomId, eventId)
    }

    override fun onUserClicked(userId: String) {
        navigator.navigateToUserDialogFragment(userId)
    }

    override fun onShowPreview(roomId: String, eventId: String) {
        navigator.navigateToShowMediaPreview(roomId, eventId)
    }

    override fun onShowEmoji(roomId: String, eventId: String, onAddEmoji: (String) -> Unit) {
        if (showNoInternetConnection()) return
        if (showErrorIfNotAbleToPost()) return
        onLocalAddEmojiCallback = onAddEmoji
        navigator.navigateToShowEmoji(roomId, eventId)
    }

    override fun onReply(roomId: String, eventId: String) {
        if (showNoInternetConnection()) return
        navigator.navigateToThread(roomId, eventId)
    }

    override fun onShare(content: PostContent) {
        if (showNoInternetConnection()) return
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

    override fun onEmojiChipClicked(
        roomId: String, eventId: String, emoji: String, isUnSend: Boolean
    ) {
        if (showNoInternetConnection()) return
        if (showErrorIfNotAbleToPost()) return
        if (isUnSend) viewModel.unSendReaction(roomId, eventId, emoji)
        else viewModel.sendReaction(roomId, eventId, emoji)
    }

    override fun onPollOptionSelected(roomId: String, eventId: String, optionId: String) {
        if (showNoInternetConnection()) return
        if (showErrorIfNotAbleToPost()) return
        viewModel.pollVote(roomId, eventId, optionId)
    }

    override fun endPoll(roomId: String, eventId: String) {
        withConfirmation(EndPoll()) { viewModel.endPoll(roomId, eventId) }
    }

    override fun onSendPost(
        roomId: String,
        postContent: CreatePostContent,
        threadEventId: String?
    ) {
        viewModel.sendPost(roomId, postContent, threadEventId)
    }

    override fun onEditPost(roomId: String, postContent: CreatePostContent, eventId: String) {
        viewModel.editPost(eventId, roomId, postContent)
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
        onLocalAddEmojiCallback?.invoke(emoji)
        onLocalAddEmojiCallback = null
        viewModel.sendReaction(roomId, eventId, emoji)
    }

    private fun stopVideoOnNewScreenOpen() {
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.timelineFragment) listAdapter.stopVideoPlayback()
        }
    }

    private fun navigateToTimelineOptions() {
        val type = if (isGroupMode) CircleRoomTypeArg.Group else CircleRoomTypeArg.Circle
        navigator.navigateToTimelineOptions(args.roomId, type, args.timelineId)
    }

    private fun scrollToTopIfMyNewPostAdded(positionStart: Int, itemCount: Int) {
        val items = viewModel.timelineEventsLiveData.value ?: emptyList()
        if (itemCount != 1) return
        if (isThread) {
            val lastItemPosition = items.size - 1
            if ((items.lastOrNull() as? Post)?.isMyPost() == true && positionStart == lastItemPosition) {
                binding.rvTimeline.layoutManager?.smoothScrollToPosition(
                    binding.rvTimeline.getRecyclerView(),
                    null,
                    lastItemPosition
                )
            }
        } else {
            if ((items.firstOrNull() as? Post)?.isMyPost() == true && positionStart == 0) {
                binding.rvTimeline.layoutManager?.smoothScrollToPosition(
                    binding.rvTimeline.getRecyclerView(), null, 0
                )
            }
        }
    }

    private fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        if (isGroupMode) onGroupUserAccessLevelChanged(powerLevelsContent)
        else onCircleUserAccessLeveChanged(powerLevelsContent)
    }

    private fun showErrorIfNotAbleToPost(): Boolean {
        val isAbleToPost = viewModel.accessLevelLiveData.value?.isCurrentUserAbleToPost() == true
        if (!isAbleToPost) showError(getString(R.string.you_can_not_post_to_this_room))
        return !isAbleToPost
    }

    private fun onGroupUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.lCreatePost.setUserAbleToPost(powerLevelsContent.isCurrentUserAbleToPost())
    }

    private fun onCircleUserAccessLeveChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.lCreatePost.setUserAbleToPost(isUserAdmin)
    }

}