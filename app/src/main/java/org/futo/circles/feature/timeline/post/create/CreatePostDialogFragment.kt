package org.futo.circles.feature.timeline.post.create

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.core.picker.MediaType
import org.futo.circles.databinding.DialogFragmentCreatePostBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.TextPostContent
import org.futo.circles.view.PreviewPostListener
import org.futo.circles.view.markdown.TextStyle
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class CreatePostDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreatePostBinding::inflate),
    PostConfigurationOptionListener, EmojiPickerListener {

    private val args: CreatePostDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentCreatePostBinding
    }
    private val viewModel by viewModel<CreatePostViewModel> {
        parametersOf(args.roomId, args.eventId, args.isEdit)
    }

    private val mediaPickerHelper = MediaPickerHelper(this, true)
    private var createPostListener: CreatePostListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createPostListener =
            parentFragmentManager.fragments.firstOrNull { it is CreatePostListener } as? CreatePostListener
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
            btnPost.apply {
                setOnClickListener {
                    sendPost()
                    onBackPressed()
                }
                setText(getString(if (args.isEdit) R.string.edit else R.string.send))
            }
            binding.vPostOptions.apply {
                setOptionsListener(this@CreatePostDialogFragment)
                showMainOptionsList(!args.isEdit)
            }
            vPostPreview.setListener(object : PreviewPostListener {
                override fun onPostContentAvailable(isAvailable: Boolean) {
                    binding.btnPost.isEnabled = isAvailable
                }
            },
                onHighlightTextStyle = { textStyle -> binding.vPostOptions.highlightStyle(textStyle) }
            )
        }
    }

    private fun setupObservers() {
        viewModel.textToEditLiveData.observeData(this) {
            binding.vPostPreview.setText(it)
        }
    }

    private fun setToolbarTitle() {
        binding.toolbar.title = when {
            args.isEdit -> getString(R.string.edit_post)
            args.userName != null -> getString(R.string.reply_to_format, args.userName)
            else -> getString(R.string.create_post)
        }
    }

    private fun onMediaSelected(uri: Uri, type: MediaType) {
        binding.vPostPreview.setMedia(uri, type)
    }

    private fun sendPost() {
        if (args.isEdit) onEditPost()
        else createPostListener?.onSendPost(
            args.roomId, binding.vPostPreview.getPostContent(), args.eventId
        )
    }

    private fun onEditPost() {
        val newMessage = (binding.vPostPreview.getPostContent() as? TextPostContent)?.text ?: return
        val eventId = args.eventId ?: return
        createPostListener?.onEditTextPost(args.roomId, newMessage, eventId)
    }

    override fun onUploadMediaClicked() {
        mediaPickerHelper.showMediaPickerDialog(
            onImageSelected = { _, uri -> onMediaSelected(uri, MediaType.Image) },
            onVideoSelected = { uri -> onMediaSelected(uri, MediaType.Video) }
        )
    }

    override fun onEmojiClicked() {
        findNavController().navigate(
            CreatePostDialogFragmentDirections.toEmojiBottomSheet(null, null)
        )
    }

    override fun onMentionClicked() {
        binding.vPostPreview.insertMention()
    }

    override fun onTextStyleSelected(textStyle: TextStyle, isSelected: Boolean) {
        binding.vPostPreview.setTextStyle(textStyle, isSelected)
    }

    override fun onAddLinkClicked() {

    }

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        binding.vPostPreview.insertEmoji(emoji)
    }
}