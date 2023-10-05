package org.futo.circles.feature.timeline

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.NetworkObserver
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.extensions.isCurrentUserAbleToPost
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.share.ShareProvider
import org.futo.circles.core.utils.debounce
import org.futo.circles.core.utils.getTimelineRoomIdOrThrow
import org.futo.circles.databinding.DialogFragmentTimelineBinding
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
import org.futo.circles.view.PostOptionsListener
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

@AndroidEntryPoint
class TimelineDialogFragment : BaseFullscreenDialogFragment(DialogFragmentTimelineBinding::inflate),
    PostOptionsListener, PostMenuListener,
    CreatePostListener, CreatePollListener, EmojiPickerListener {

    private val args: TimelineDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<TimelineViewModel>()

    private val isGroupMode by lazy { args.type == CircleRoomTypeArg.Group }
    private val isThread by lazy { args.threadEventId != null }

    private val timelineId by lazy {
        if (args.type == CircleRoomTypeArg.Circle) getTimelineRoomIdOrThrow(args.roomId)
        else args.roomId
    }
    private val binding by lazy {
        getBinding() as DialogFragmentTimelineBinding
    }

    private val loadMoreDebounce by lazy {
        debounce<Unit>(
            scope = lifecycleScope,
            destinationFunction = {
                if (viewModel.loadMore()) binding.rvTimeline.setIsPageLoading(true)
            }
        )
    }

    private val submitDataDebounce by lazy {
        debounce<List<Post>>(
            scope = lifecycleScope,
            destinationFunction = {
                listAdapter.submitList(it)
                binding.rvTimeline.setIsPageLoading(false)
                viewModel.markTimelineAsRead(args.roomId, isGroupMode)
            }
        )
    }

    private val listAdapter by lazy {
        TimelineAdapter(
            getCurrentUserPowerLevel(args.roomId),
            this,
            isThread
        ) { loadMoreDebounce(Unit) }
    }
    private val navigator by lazy { TimelineNavigator(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        setupMenu()
    }

    private fun setupViews() {
        binding.rvTimeline.apply {
            adapter = listAdapter
            getRecyclerView().apply {
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
                itemAnimator = null
                setItemViewCacheSize(20)
                recycledViewPool.setMaxRecycledViews(PostContentType.TEXT_CONTENT.ordinal, 20)
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        binding.lCreatePost.setUp(object : CreatePostViewListener {
            override fun onCreatePoll() {
                navigator.navigateToCreatePoll(timelineId)
            }

            override fun onCreatePost() {
                navigator.navigateToCreatePost(timelineId, args.threadEventId)
            }
        }, binding.rvTimeline.getRecyclerView(), isThread)
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
            setOnClickListener { navigator.navigateToTimelineOptions(args.roomId, args.type) }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    org.futo.circles.core.R.id.settings -> navigator.navigateToTimelineOptions(
                        args.roomId,
                        args.type
                    )
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
            submitDataDebounce(it)
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
                context?.let { showSuccess(it.getString(R.string.user_ignored)) }
            })
        viewModel.unSendReactionLiveData.observeResponse(this)

        viewModel.profileLiveData?.observeData(this) {
            it.getOrNull()?.let { binding.lCreatePost.setUserInfo(it) }
        }
    }

    override fun onShowMenuClicked(roomId: String, eventId: String) {
        if (!showNoInternetConnection()) return
        navigator.navigatePostMenu(roomId, eventId)
    }

    override fun onUserClicked(userId: String) {
        navigator.navigateToUserDialogFragment(userId)
    }

    override fun onShowPreview(roomId: String, eventId: String) {
        navigator.navigateToShowMediaPreview(roomId, eventId)
    }

    override fun onShowEmoji(roomId: String, eventId: String) {
        if (showNoInternetConnection()) return
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
        if (viewModel.accessLevelLiveData.value?.isCurrentUserAbleToPost() != true) {
            showError(getString(R.string.you_can_not_post_to_this_room))
            return
        }
        if (isUnSend) viewModel.unSendReaction(roomId, eventId, emoji)
        else viewModel.sendReaction(roomId, eventId, emoji)
    }

    override fun onPollOptionSelected(roomId: String, eventId: String, optionId: String) {
        if (showNoInternetConnection()) return
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
        viewModel.sendReaction(roomId, eventId, emoji)
    }

    private fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        if (isGroupMode) onGroupUserAccessLevelChanged(powerLevelsContent)
        else onCircleUserAccessLeveChanged(powerLevelsContent)
        listAdapter.updateUserPowerLevel(getCurrentUserPowerLevel(args.roomId))
    }

    private fun onGroupUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        binding.lCreatePost.setUserAbleToPost(powerLevelsContent.isCurrentUserAbleToPost())
    }

    private fun onCircleUserAccessLeveChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.lCreatePost.setUserAbleToPost(isUserAdmin)
    }

}