package com.futo.circles.feature.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.futo.circles.R
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.databinding.CreatePostBottomSheetBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.view.PreviewPostListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePostBottomSheet : BottomSheetDialogFragment() {

    private var binding: CreatePostBottomSheetBinding? = null
    private val viewModel by viewModel<CreatePostViewModel>()
    private val imagePickerHelper = ImagePickerHelper(this)

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
        binding?.ivClose?.setOnClickListener { dismiss() }
        binding?.ivText?.setOnClickListener { viewModel.setIsImagePost(false) }
        binding?.ivImage?.setOnClickListener { viewModel.setIsImagePost(true) }
        binding?.btnPost?.setOnClickListener {
            dismiss()
        }
        binding?.vPostPreview?.setListener(object : PreviewPostListener {
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

    private fun setupObservers() {
        viewModel.isImagePostLiveData.observeData(this) {
            handlePostType(it)
        }
        viewModel.selectedImageLiveData.observeData(this) {
            binding?.vPostPreview?.setImage(it)
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
        val isImagePost = binding?.vPostPreview?.isImagePostSelected ?: false
        binding?.btnPost?.isEnabled = if (isImagePost) viewModel.selectedImageLiveData.value != null
        else binding?.vPostPreview?.getText()?.isNotBlank() == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}