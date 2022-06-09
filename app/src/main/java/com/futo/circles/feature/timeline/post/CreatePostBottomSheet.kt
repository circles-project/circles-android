package com.futo.circles.feature.timeline.post

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.image_picker.ImagePickerHelper
import com.futo.circles.databinding.CreatePostBottomSheetBinding
import com.futo.circles.view.PreviewPostListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface CreatePostListener {
    fun onSendTextPost(roomId: String, message: String, threadEventId: String?)
    fun onSendImagePost(roomId: String, uri: Uri, threadEventId: String?)
}

class CreatePostBottomSheet : BottomSheetDialogFragment() {

    private var binding: CreatePostBottomSheetBinding? = null
    private val args: CreatePostBottomSheetArgs by navArgs()

    private val imagePickerHelper = ImagePickerHelper(this)
    private var createPostListener: CreatePostListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createPostListener =
            parentFragmentManager.fragments.firstOrNull { it is CreatePostListener } as? CreatePostListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = CreatePostBottomSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            (it as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        setupViews()
    }

    private fun setupViews() {
        binding?.let { binding ->
            with(binding) {
                args.userName?.let {
                    tvTitle.text = context?.getString(R.string.reply_to_format, it)
                }
                ivClose.setOnClickListener { dismiss() }
                btnPost.setOnClickListener {
                    sendPost()
                    dismiss()
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
    }

    private fun sendPost() {
        if (binding?.vPostPreview?.isImageContentSelected() == true) {
            binding?.vPostPreview?.getImageUri()
                ?.let { createPostListener?.onSendImagePost(args.roomId, it, args.eventId) }
        } else {
            binding?.vPostPreview?.getText()
                ?.let { createPostListener?.onSendTextPost(args.roomId, it, args.eventId) }
        }
    }

    private fun handleSendButtonEnabled() {
        binding?.btnPost?.isEnabled =
            if (binding?.vPostPreview?.isImageContentSelected() == true) binding?.vPostPreview?.getImageUri() != null
            else binding?.vPostPreview?.getText()?.isNotBlank() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}