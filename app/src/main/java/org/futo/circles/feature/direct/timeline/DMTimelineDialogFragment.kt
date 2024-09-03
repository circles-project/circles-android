package org.futo.circles.feature.direct.timeline

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.dpToPx
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.feature.share.ShareProvider
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PostContent
import org.futo.circles.databinding.DialogFragmentDmTimelineBinding
import org.futo.circles.feature.direct.timeline.list.DMTimelineAdapter
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.direct.timeline.listeners.SendDmMessageListener
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.RemovePost


@AndroidEntryPoint
class DMTimelineDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentDmTimelineBinding>(DialogFragmentDmTimelineBinding::inflate),
    EmojiPickerListener, SendDmMessageListener, DmOptionsListener {

    private val args: DMTimelineDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<DMTimelineViewModel>()

    private val videoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }
    private val mediaPickerHelper = MediaPickerHelper(this, isVideoAvailable = true)

    private val navigator by lazy { DMTimelineNavigator(this) }
    private val listAdapter by lazy {
        DMTimelineAdapter(this, videoPlayer).apply {
            setHasStableIds(true)
        }
    }

    private var onLocalAddEmojiCallback: ((String) -> Unit)? = null


    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setupViews()
        setupObservers()
        stopVideoOnNewScreenOpen()
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
            getRecyclerView().apply {
                (layoutManager as? LinearLayoutManager)?.stackFromEnd = true
                isNestedScrollingEnabled = false
                clipToPadding = false
                setPadding(paddingLeft, paddingTop, paddingRight, context.dpToPx(80))
            }
            addPageEndListener { viewModel.loadMore() }
        }

        binding.vSendMessage.setup(this)
    }


    private fun setupObservers() {
        viewModel.userTitleLiveData?.observeData(this) { user ->
            user ?: return@observeData
            binding.vUserLayout.apply {
                bind(user)
                setOnClickListener { navigator.navigateToUserPage(user.id) }
            }
        }
        viewModel.dmTimelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
            viewModel.markTimelineAsRead(args.roomId)
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.saveToDeviceLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.saved)) }
        }

        viewModel.unSendReactionLiveData.observeResponse(this)
    }

    override fun onShowMenuClicked(eventId: String) {
        if (showNoInternetConnection()) return
        navigator.navigateToDmMenu(args.roomId, eventId)
    }

    override fun onShowPreview(eventId: String) {
        navigator.navigateToShowMediaPreview(args.roomId, eventId)
    }

    override fun onShowEmoji(eventId: String, onAddEmoji: (String) -> Unit) {
        if (showNoInternetConnection()) return
        onLocalAddEmojiCallback = onAddEmoji
        navigator.navigateToEmojiPicker(args.roomId, eventId)
    }

    override fun onReply(message: String) {
        binding.vSendMessage.setReplyText(message)
    }

    override fun onShare(content: PostContent) {
        viewModel.sharePostContent(content)
    }

    override fun onRemove(eventId: String) {
        if (showNoInternetConnection()) return
        withConfirmation(RemovePost()) { viewModel.removeMessage(args.roomId, eventId) }
    }

    override fun onEditActionClicked(eventId: String, message: String) {
        if (showNoInternetConnection()) return
        binding.vSendMessage.setTextForEdit(message) { newMessage ->
            viewModel.editTextMessage(eventId, args.roomId, newMessage)
        }
    }

    override fun onSaveToDevice(content: PostContent) {
        viewModel.saveToDevice(content)
    }

    override fun onEmojiChipClicked(eventId: String, emoji: String, isUnSend: Boolean) {
        if (showNoInternetConnection()) return
        if (isUnSend) viewModel.unSendReaction(args.roomId, eventId, emoji)
        else viewModel.sendReaction(args.roomId, eventId, emoji)
    }


    private fun stopVideoOnNewScreenOpen() {
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.DMTimelineDialogFragment) listAdapter.stopVideoPlayback()
        }
    }

    private fun scrollToBottomOnMyNewPostAdded() {
        with(binding.rvTimeline) {
            adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    adapter?.itemCount?.let { count ->
                        layoutManager?.scrollToPosition(count - 1)
                    }
                    adapter?.unregisterAdapterDataObserver(this)
                }
            })
        }
    }

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        eventId?.let {
            onLocalAddEmojiCallback?.invoke(emoji)
            onLocalAddEmojiCallback = null
            viewModel.sendReaction(args.roomId, eventId, emoji)
        } ?: run {
            binding.vSendMessage.insertEmojiIntoMessage(emoji)
        }
    }

    override fun onAddEmojiToMessageClicked() {
        navigator.navigateToEmojiPicker()
    }

    override fun onSendTextMessageClicked(message: String) {
        if (showNoInternetConnection()) return
        viewModel.sendTextMessageDm(message) { scrollToBottomOnMyNewPostAdded() }
    }

    override fun onSendMediaButtonClicked() {
        if (showNoInternetConnection()) return
        mediaPickerHelper.showMediaPickerDialog(
            onImageSelected = { _, uri ->
                viewModel.sendMediaDm(uri, MediaType.Image) { scrollToBottomOnMyNewPostAdded() }
            },
            onVideoSelected = { uri ->
                viewModel.sendMediaDm(uri, MediaType.Video) { scrollToBottomOnMyNewPostAdded() }
            }
        )
    }
}