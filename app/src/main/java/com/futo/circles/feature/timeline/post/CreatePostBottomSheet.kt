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
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.databinding.CreatePostBottomSheetBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.view.PreviewPostListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

interface CreatePostListener {
    fun onSendTextPost(roomId: String, message: String, threadEventId: String?)
    fun onSendImagePost(roomId: String, uri: Uri, threadEventId: String?)
}

class CreatePostBottomSheet : BottomSheetDialogFragment() {

    private var binding: CreatePostBottomSheetBinding? = null
    private val args: CreatePostBottomSheetArgs by navArgs()
    private val viewModel by viewModel<CreatePostViewModel>()
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
        setupObservers()
    }

    private fun setupViews() {
        binding?.let { binding ->
            with(binding) {
                args.userName?.let {
                    tvTitle.text = context?.getString(R.string.reply_to_format, it)
                }
                ivClose.setOnClickListener { dismiss() }
                ivText.setOnClickListener { viewModel.setIsImagePost(false) }
                ivImage.setOnClickListener { viewModel.setIsImagePost(true) }
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
                            viewModel.setImageUri(uri)
                            handleSendButtonEnabled()
                        })
                    }
                })
            }
        }
    }

    private fun setupObservers() {
        viewModel.isImagePostLiveData.observeData(this) {
            handlePostType(it)
        }
        viewModel.selectedImageLiveData.observeData(this) {
            binding?.vPostPreview?.setImage(it)
        }
    }

    private fun sendPost() {
        if (viewModel.isImagePostSelected()) {
            viewModel.getImageUri()
                ?.let { createPostListener?.onSendImagePost(args.roomId, it, args.eventId) }
        } else {
            binding?.vPostPreview?.getText()
                ?.let { createPostListener?.onSendTextPost(args.roomId, it, args.eventId) }
        }
    }

    private fun handlePostType(isImagePost: Boolean) {
        binding?.vPostPreview?.setIsImagePost(isImagePost)
        val activeColor = context?.getColor(R.color.blue) ?: return
        val inActiveColor = context?.getColor(R.color.gray) ?: return
        binding?.ivText?.setColorFilter(if (isImagePost) inActiveColor else activeColor)
        binding?.ivImage?.setColorFilter(if (isImagePost) activeColor else inActiveColor)
        handleSendButtonEnabled()
    }

    private fun handleSendButtonEnabled() {
        binding?.btnPost?.isEnabled =
            if (viewModel.isImagePostSelected()) viewModel.getImageUri() != null
            else binding?.vPostPreview?.getText()?.isNotBlank() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}