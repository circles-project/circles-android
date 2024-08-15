package org.futo.circles.feature.timeline.post.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.model.TextContent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.databinding.DialogFragmentCreatePostBinding
import org.futo.circles.feature.timeline.list.MediaProgressHelper
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.CreatePostContent
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

@AndroidEntryPoint
class CreatePostDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentCreatePostBinding>(DialogFragmentCreatePostBinding::inflate),
    PreviewPostListener,
    EmojiPickerListener {

    private val args: CreatePostDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<CreatePostViewModel>()

    private val mediaPickerHelper = MediaPickerHelper(this, isVideoAvailable = true)

    private val uploadMediaTracker =
        MatrixSessionProvider.currentSession?.contentUploadProgressTracker()

    private val uploadListener: ContentUploadStateTracker.UpdateListener by lazy {
        MediaProgressHelper.getUploadListener(binding.vLoadingView)
    }

    private var sentPostListener: PostSentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sentPostListener =
            parentFragmentManager.fragments.lastOrNull { it is PostSentListener } as? PostSentListener
    }


    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        setToolbarTitle()
        binding.vPostPreview.setup(this@CreatePostDialogFragment, args.roomId, args.isEdit)
    }

    private fun setupObservers() {
        viewModel.postToEditContentLiveData.observeData(this) {
            when (it.type) {
                PostContentType.IMAGE_CONTENT, PostContentType.VIDEO_CONTENT ->
                    binding.vPostPreview.setMediaFromExistingPost(it as MediaContent)

                else -> binding.vPostPreview.setText((it as TextContent).message)
            }
        }
        viewModel.sendEventObserverLiveData.observeData(this) { (eventId, sendStateLiveData) ->
            uploadMediaTracker?.track(eventId, uploadListener)
            sendStateLiveData.observeData(this) { sendState ->
                setEnabledViews(!sendState.isSending())
                if (sendState.isSending()) {
                    binding.vLoadingView.setProgress(ResLoadingData(R.string.sending))
                    binding.vLoadingView.visible()
                } else if (sendState.isSent()) {
                    if (!args.isEdit) sentPostListener?.onPostSent()
                    binding.vLoadingView.gone()
                    dismiss()
                } else {
                    binding.vLoadingView.gone()
                    uploadMediaTracker?.clear()
                    showError(getString(R.string.failed_to_send))
                }
            }
        }
    }

    private fun setToolbarTitle() {
        binding.toolbar.title =
            getString(if (args.isEdit) R.string.edit_post else R.string.create_new_post)
    }

    private fun onMediaSelected(uri: Uri, type: MediaType) {
        binding.vPostPreview.setMedia(uri, type)
    }


    override fun onUploadMediaClicked() {
        mediaPickerHelper.showMediaPickerDialog(
            onImageSelected = { _, uri -> onMediaSelected(uri, MediaType.Image) },
            onVideoSelected = { uri -> onMediaSelected(uri, MediaType.Video) }
        )
    }

    override fun onEmojiClicked() {
        findNavController().navigateSafe(
            CreatePostDialogFragmentDirections.toEmojiBottomSheet(null, null)
        )
    }

    override fun onAddLinkClicked() {
        AddLinkDialog(requireContext()) { title, link ->
            binding.vPostPreview.insertLink(title, link)
        }.show()
    }

    override fun onSendClicked(content: CreatePostContent) {
        if (showNoInternetConnection()) return
        viewModel.onSendAction(content)
    }

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        binding.vPostPreview.insertEmoji(emoji)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        uploadMediaTracker?.clear()
    }
}