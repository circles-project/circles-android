package com.futo.circles.feature.setup_profile

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.databinding.SetupProfileFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupProfileFragment : Fragment(R.layout.setup_profile_fragment), HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(SetupProfileFragmentBinding::bind)
    private val viewModel by viewModel<SetupProfileViewModel>()
    private val imagePickerHelper = ImagePickerHelper(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnSkip.setOnClickListener { navigateToSetupCircles() }
            btnSave.setOnClickListener {
                viewModel.saveProfileInfo(
                    tilDisplayName.editText?.text?.trim()?.toString()
                )
            }
            ivProfile.setOnClickListener {
                imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
                    viewModel.setProfileImageUri(uri)
                })
            }
            tilDisplayName.editText?.doAfterTextChanged { setSaveButtonEnabled() }
            tilDisplayName.setEndIconOnClickListener {
                showDialog(
                    R.string.display_name,
                    R.string.display_name_explanation
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.profileImageLiveData.observeData(this) {
            setSaveButtonEnabled()
            binding.ivProfile.setImageURI(it)
        }
        viewModel.saveProfileResponseLiveData.observeResponse(
            this,
            success = { navigateToSetupCircles() }
        )
    }

    private fun navigateToSetupCircles() {
        findNavController().navigate(SetupProfileFragmentDirections.toSetupCirclesFragment())
    }

    private fun setSaveButtonEnabled() {
        binding.btnSave.isEnabled = viewModel.isProfileImageChosen() ||
                binding.tilDisplayName.editText?.text.isNullOrEmpty() != true
    }
}