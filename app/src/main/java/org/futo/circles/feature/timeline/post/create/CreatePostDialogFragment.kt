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
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.DialogFragmentCreatePostBinding
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.CreatePostContent
import java.util.*

@AndroidEntryPoint
class CreatePostDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreatePostBinding::inflate),
    PreviewPostListener, EmojiPickerListener {

    private val args: CreatePostDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentCreatePostBinding
    }
    private val viewModel by viewModels<CreatePostViewModel>()

    private val mediaPickerHelper = MediaPickerHelper(this, isVideoAvailable = true)
    private var createPostListener: CreatePostListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createPostListener =
            parentFragmentManager.fragments.lastOrNull { it is CreatePostListener } as? CreatePostListener
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
        with(binding) {
            vPostPreview.setup(this@CreatePostDialogFragment, args.roomId, args.isEdit)
        }
    }

    private fun setupObservers() {
        viewModel.postToEditContentLiveData.observeData(this) {
            when (it.type) {
                PostContentType.IMAGE_CONTENT, PostContentType.VIDEO_CONTENT ->
                    binding.vPostPreview.setMediaFromExistingPost(it as MediaContent)

                else -> binding.vPostPreview.setText((it as TextContent).message)
            }
        }
    }

    private fun setToolbarTitle() {
        binding.toolbar.title = when {
            args.isEdit -> getString(R.string.edit_post)
            else -> getString(R.string.create_post)
        }
    }

    private fun onMediaSelected(uri: Uri, type: MediaType) {
        binding.vPostPreview.setMedia(uri, type)
    }

    private fun sendPost(content: CreatePostContent) {
        if (args.isEdit) onEditPost(content)
        else createPostListener?.onSendPost(
            args.roomId, content, args.eventId
        )
    }

    private fun onEditPost(content: CreatePostContent) {
        val eventId = args.eventId ?: return
        createPostListener?.onEditPost(args.roomId, content, eventId)
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
        if (!NetworkObserver.isConnected()) {
            showError(getString(org.futo.circles.core.R.string.no_internet_connection))
            return
        }
        sendPost(content)
        dismiss()
    }

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        binding.vPostPreview.insertEmoji(emoji)
    }
}