package org.futo.circles.feature.timeline.post

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.image_picker.ImagePickerHelper
import org.futo.circles.databinding.CreatePostDialogFragmentBinding
import org.futo.circles.model.CreatePostContent
import org.futo.circles.view.PreviewPostListener
import java.text.DateFormat
import java.util.*

interface CreatePostListener {
    fun onSendPost(roomId: String, postContent: CreatePostContent, threadEventId: String?)
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
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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
            tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date())
            ivMedia.setOnClickListener {
                imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
                    vPostPreview.setImage(uri)
                })
            }
            vPostPreview.setListener(object : PreviewPostListener {
                override fun onPostContentAvailable(isAvailable: Boolean) {
                    binding.btnPost.isEnabled = isAvailable
                }
            })
        }
    }

    private fun sendPost() {
        createPostListener?.onSendPost(
            args.roomId, binding.vPostPreview.getPostContent(), args.eventId
        )
    }
}