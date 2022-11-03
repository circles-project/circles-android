package org.futo.circles.feature.timeline.post

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.core.picker.MediaType
import org.futo.circles.databinding.DialogFragmentCreatePostBinding
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.model.CreatePostContent
import org.futo.circles.view.PreviewPostListener
import java.text.DateFormat
import java.util.*

interface CreatePostListener {
    fun onSendPost(roomId: String, postContent: CreatePostContent, threadEventId: String?)
}

class CreatePostDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreatePostBinding::inflate) {

    private val args: CreatePostDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentCreatePostBinding
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
    }

    private fun setupViews() {
        with(binding) {
            args.userName?.let {
                toolbar.title = context?.getString(R.string.reply_to_format, it)
            }
            btnPost.setOnClickListener {
                sendPost()
                onBackPressed()
            }
            btnUploadMedia.setOnClickListener {
                mediaPickerHelper.showMediaPickerDialog(
                    onImageSelected = { _, uri -> onMediaSelected(uri, MediaType.Image) },
                    onVideoSelected = { uri -> onMediaSelected(uri, MediaType.Video) }
                )
            }
            vPostPreview.setListener(object : PreviewPostListener {
                override fun onPostContentAvailable(isAvailable: Boolean) {
                    binding.btnPost.isEnabled = isAvailable
                }
            })
        }
    }

    private fun onMediaSelected(uri: Uri, type: MediaType) {
        binding.vPostPreview.setMedia(uri, type)
    }

    private fun sendPost() {
        createPostListener?.onSendPost(
            args.roomId, binding.vPostPreview.getPostContent(), args.eventId
        )
    }
}