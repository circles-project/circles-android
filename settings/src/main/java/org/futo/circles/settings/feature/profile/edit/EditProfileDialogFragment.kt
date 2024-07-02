package org.futo.circles.settings.feature.profile.edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.settings.R
import org.futo.circles.settings.databinding.DialogFragmentEditProfileBinding
import org.futo.circles.settings.view.EditEmailView
import org.matrix.android.sdk.api.session.identity.ThreePid
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class EditProfileDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentEditProfileBinding>(DialogFragmentEditProfileBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<EditProfileViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            ivProfile.setOnClickListener { showImagePicker() }
            btnChangeIcon.setOnClickListener { showImagePicker() }
            tilName.editText?.doAfterTextChanged {
                it?.let { onProfileDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.update(tilName.getText())
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivProfile.setImageURI(it)
            onProfileDataChanged()
        }
        viewModel.editProfileResponseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.profile_updated))
                onBackPressed()
            }
        )
        viewModel.profileLiveData.observeData(this) {
            it?.let { user -> setInitialUserInfo(user) }
        }
        viewModel.isProfileDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
        viewModel.emailsLiveData.observeData(this) { emails ->
            setupEmailList(emails)
        }
    }

    private fun showImagePicker() {
        mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    private fun setInitialUserInfo(user: User) {
        with(binding) {
            ivProfile.loadUserProfileIcon(user.avatarUrl, user.userId)
            tilName.editText?.setText(user.displayName)
            tvUserId.text = user.userId
        }
    }

    private fun onProfileDataChanged() {
        viewModel.handleProfileDataUpdate(binding.tilName.getText())
    }

    private fun setupEmailList(emails: List<ThreePid>) {
        binding.lEmails.removeAllViews()
        emails.forEach { email ->
            binding.lEmails.addView(EditEmailView(requireContext()).apply {
                setData(email.value) { viewModel.removeEmail(it) }
            })
        }
    }
    
}