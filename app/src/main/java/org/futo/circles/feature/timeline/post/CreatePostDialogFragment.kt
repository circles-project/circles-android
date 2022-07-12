package org.futo.circles.feature.timeline.post

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.image_picker.ImagePickerHelper
import org.futo.circles.databinding.CreatePostDialogFragmentBinding
import org.futo.circles.view.PreviewPostListener

interface CreatePostListener {
    fun onSendTextPost(roomId: String, message: String, threadEventId: String?)
    fun onSendImagePost(roomId: String, uri: Uri, threadEventId: String?)
}

class CreatePostDialogFragment :
    BaseFullscreenDialogFragment(CreatePostDialogFragmentBinding::inflate) {

    private val args: CreatePostDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as CreatePostDialogFragmentBinding
    }

    private val imagePickerHelper = ImagePickerHelper(this)
    private var createPostListener: CreatePostListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createPostListener =
            parentFragmentManager.fragments.firstOrNull { it is CreatePostListener } as? CreatePostListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            args.userName?.let {
                toolbar.title = context?.getString(R.string.reply_to_format, it)
            }
            btnPost.setOnClickListener {
                sendPost()
                activity?.onBackPressed()
            }
            vPostPreview.setListener(object : PreviewPostListener {
                override fun onTextChanged(text: String) {
                    handleSendButtonEnabled()
                }

                override fun onPickImageClicked() {
                    imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
                        vPostPreview.setImage(uri)
                        handleSendButtonEnabled()
                    })
                }

                override fun onPostTypeChanged(isImagePost: Boolean) {
                    handleSendButtonEnabled()
                }
            })
        }
    }

    private fun sendPost() {
        if (binding.vPostPreview.isImageContentSelected()) {
            binding.vPostPreview.getImageUri()
                ?.let { createPostListener?.onSendImagePost(args.roomId, it, args.eventId) }
        } else {
            createPostListener?.onSendTextPost(
                args.roomId,
                binding.vPostPreview.getText(),
                args.eventId
            )
        }
    }

    private fun handleSendButtonEnabled() {
        binding.btnPost.isEnabled =
            if (binding.vPostPreview.isImageContentSelected()) binding.vPostPreview.getImageUri() != null
            else binding.vPostPreview.getText().isNotBlank()
    }
}