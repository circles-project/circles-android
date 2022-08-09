package org.futo.circles.feature.settings.edit_profile

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.EditProfileDialogFragmentBinding
import org.futo.circles.extensions.*
import org.futo.circles.mapping.notEmptyDisplayName
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.matrix.android.sdk.api.session.user.model.User

class EditProfileDialogFragment :
    BaseFullscreenDialogFragment(EditProfileDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<EditProfileViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this)

    private val binding by lazy {
        getBinding() as EditProfileDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
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
                showSuccess(getString(R.string.profile_updated), true)
                activity?.onBackPressed()
            }
        )
        viewModel.profileLiveData.observeData(this) {
            it.getOrNull()?.let { user -> setInitialUserInfo(user) }
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
            ivProfile.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tilName.editText?.setText(user.displayName)
            tilUserId.editText?.setText(user.userId)
        }
    }

    private fun onProfileDataChanged() {
        viewModel.handleProfileDataUpdate(binding.tilName.getText())
    }
}