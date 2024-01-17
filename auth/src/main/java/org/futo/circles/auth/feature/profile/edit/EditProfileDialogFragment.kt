package org.futo.circles.auth.feature.profile.edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentEditProfileBinding
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class EditProfileDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentEditProfileBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<EditProfileViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this)

    private val binding by lazy {
        getBinding() as DialogFragmentEditProfileBinding
    }

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
            setAlwaysDisabledViews(listOf(tilUserId, tilContactInfo))
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
        viewModel.threePidLiveData.observeData(this) {
            binding.tilContactInfo.editText?.setText(it.firstOrNull()?.value ?: "")
        }
        viewModel.isProfileDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
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
            tilUserId.editText?.setText(user.userId)
        }
    }

    private fun onProfileDataChanged() {
        viewModel.handleProfileDataUpdate(binding.tilName.getText())
    }
}