package com.futo.circles.feature.create_group

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import com.futo.circles.R
import com.futo.circles.base.BaseFullscreenDialogFragment
import com.futo.circles.databinding.CreateGroupDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.showError
import com.futo.circles.pick_image.PickImageDialog
import com.futo.circles.pick_image.PickImageDialogListener
import com.futo.circles.pick_image.PickImageMethod
import com.github.dhaval2404.imagepicker.ImagePicker
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateGroupDialogFragment :
    BaseFullscreenDialogFragment(CreateGroupDialogFragmentBinding::inflate),
    PickImageDialogListener {

    private val viewModel by viewModel<CreateGroupViewModel>()

    private val pickImageDialog by lazy {
        PickImageDialog(this.requireContext(), this)
    }

    private val binding by lazy {
        getBinding() as CreateGroupDialogFragmentBinding
    }

    private val startForGroupImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.data?.let { viewModel.setImageUri(it) }
                        ?: showError(getString(R.string.unexpected_error))
                }
                ImagePicker.RESULT_ERROR -> showError(ImagePicker.getError(data))
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            ivGroup.setOnClickListener { pickImageDialog.show() }
            tilGroupName.editText?.doAfterTextChanged {
                it?.let { binding.btnCreate.setButtonEnabled(it.isNotEmpty()) }
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivGroup.setImageURI(it)
        }
    }

    override fun onPickMethodSelected(method: PickImageMethod) {
        ImagePicker.with(this)
            .cropSquare()
            .apply { if (method == PickImageMethod.Camera) this.cameraOnly() else this.galleryOnly() }
            .createIntent {
                startForGroupImageResult.launch(it)
            }
    }
}