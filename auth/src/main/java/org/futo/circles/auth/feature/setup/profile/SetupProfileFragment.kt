package org.futo.circles.auth.feature.setup.profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentSetupProfileBinding
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper

@AndroidEntryPoint
class SetupProfileFragment :
    BaseBindingFragment<FragmentSetupProfileBinding>(FragmentSetupProfileBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<SetupProfileViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), org.futo.circles.core.R.color.default_background)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnSkip.setOnClickListener { navigateToHome() }
            btnSave.setOnClickListener {
                startLoading(btnSave)
                viewModel.saveProfileInfo(tilDisplayName.getText())
            }
            ivProfile.setOnClickListener {
                mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
                    viewModel.setProfileImageUri(uri)
                })
            }
            tilDisplayName.editText?.doAfterTextChanged { setSaveButtonEnabled() }
        }
    }

    private fun setupObservers() {
        viewModel.profileImageLiveData.observeData(this) {
            setSaveButtonEnabled()
            binding.ivProfile.setImageURI(it)
        }
        viewModel.saveProfileResponseLiveData.observeResponse(
            this,
            success = { navigateToHome() }
        )
    }

    private fun navigateToHome() {
        findNavController().navigateSafe(SetupProfileFragmentDirections.toHomeFragment())
    }

    private fun setSaveButtonEnabled() {
        binding.btnSave.isEnabled = viewModel.isProfileImageChosen() ||
                binding.tilDisplayName.getText().isNotEmpty()
    }
}