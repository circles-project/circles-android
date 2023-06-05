package org.futo.circles.auth.feature.sign_up.setup_profile

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentSetupProfileBinding
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.picker.DeviceMediaPickerHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile), HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentSetupProfileBinding::bind)
    private val viewModel by viewModel<SetupProfileViewModel>()
    private val deviceMediaPickerHelper = DeviceMediaPickerHelper(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnSkip.setOnClickListener { navigateToSetupCircles() }
            btnSave.setOnClickListener {
                startLoading(btnSave)
                viewModel.saveProfileInfo(tilDisplayName.getText())
            }
            ivProfile.setOnClickListener {
                deviceMediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
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