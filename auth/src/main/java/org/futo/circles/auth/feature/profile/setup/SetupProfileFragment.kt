package org.futo.circles.auth.feature.profile.setup

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentSetupProfileBinding
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper

@AndroidEntryPoint
class SetupProfileFragment : Fragment(R.layout.fragment_setup_profile), HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentSetupProfileBinding::bind)
    private val viewModel by viewModels<SetupProfileViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this, isGalleryAvailable = false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            tilDisplayName.setEndIconOnClickListener {
                showDialog(
                    R.string.display_name,
                    R.string.display_name_explanation
                )
            }
        }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) {
            setEnabledViews(it, listOf(binding.btnSkip))
        }
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
                binding.tilDisplayName.editText?.text.isNullOrEmpty() != true
    }
}