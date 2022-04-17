package com.futo.circles.feature.configure_group

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.ConfigureGroupDialogFragmentBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.group_invite.InviteMembersDialogFragmentArgs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class ConfigureGroupDialogFragment :
    BaseFullscreenDialogFragment(ConfigureGroupDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ConfigureGroupViewModel> { parametersOf(args.roomId) }
    private val imagePickerHelper = ImagePickerHelper(this)

    private val binding by lazy {
        getBinding() as ConfigureGroupDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }


    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            ivGroup.setOnClickListener { showImagePicker() }
            btnChangeIcon.setOnClickListener { showImagePicker() }
            tilGroupName.editText?.doAfterTextChanged {
                it?.let { onGroupDataChanged() }
            }
            tilGroupTopic.editText?.doAfterTextChanged {
                it?.let { onGroupDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.updateGroup(
                    tilGroupName.editText?.text?.toString()?.trim() ?: "",
                    tilGroupTopic.editText?.text?.toString()?.trim() ?: ""
                )
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivGroup.setImageURI(it)
            onGroupDataChanged()
        }
        viewModel.updateGroupResponseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.group_updated), true)
                activity?.onBackPressed()
            }
        )
        viewModel.groupSummaryLiveData.observeData(this) {
            it?.let { setInitialGroupData(it) }
        }
        viewModel.isGroupDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
    }

    private fun showImagePicker() {
        imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    private fun setInitialGroupData(room: RoomSummary) {
        binding.ivGroup.loadProfileIcon(room.avatarUrl, room.displayName)
        binding.tilGroupName.editText?.setText(room.displayName)
        binding.tilGroupTopic.editText?.setText(room.topic)
    }

    private fun onGroupDataChanged() {
        viewModel.handleGroupDataUpdate(
            binding.tilGroupName.editText?.text?.toString()?.trim() ?: "",
            binding.tilGroupTopic.editText?.text?.toString()?.trim() ?: ""
        )
    }
}