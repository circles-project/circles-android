package com.futo.circles.feature.update_room

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.core.matrix.room.CircleRoomTypeArg
import com.futo.circles.databinding.UpdateRoomDialogFragmentBinding
import com.futo.circles.extensions.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class UpdateRoomDialogFragment :
    BaseFullscreenDialogFragment(UpdateRoomDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: UpdateRoomDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<UpdateRoomViewModel> { parametersOf(args.roomId) }
    private val imagePickerHelper = ImagePickerHelper(this)
    private val isGroupUpdate by lazy { args.type == CircleRoomTypeArg.Group }

    private val binding by lazy {
        getBinding() as UpdateRoomDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }


    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            toolbar.title =
                context?.getString(if (isGroupUpdate) R.string.configure_group else R.string.configure_circle)
            tvNameHeader.text =
                context?.getString(if (isGroupUpdate) R.string.group_name else R.string.circle_name)
            tvEncryptionWarning.text =
                context?.getString(if (isGroupUpdate) R.string.group_encryption_warning else R.string.circle_encryption_warning)
            topicViewGroup.setIsVisible(isGroupUpdate)
            ivCover.setOnClickListener { showImagePicker() }
            btnChangeIcon.setOnClickListener { showImagePicker() }
            tilName.editText?.doAfterTextChanged {
                it?.let { onRoomDataChanged() }
            }
            tilTopic.editText?.doAfterTextChanged {
                it?.let { onRoomDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.update(
                    tilName.editText?.text?.toString()?.trim() ?: "",
                    tilTopic.editText?.text?.toString()?.trim() ?: ""
                )
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
            onRoomDataChanged()
        }
        viewModel.updateGroupResponseLiveData.observeResponse(this,
            success = {
                showSuccess(
                    getString(if (isGroupUpdate) R.string.group_updated else R.string.circle_updated),
                    true
                )
                activity?.onBackPressed()
            }
        )
        viewModel.groupSummaryLiveData.observeData(this) {
            it?.let { setInitialGroupData(it) }
        }
        viewModel.isRoomDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
    }

    private fun showImagePicker() {
        imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    private fun setInitialGroupData(room: RoomSummary) {
        binding.ivCover.loadProfileIcon(room.avatarUrl, room.displayName)
        binding.tilName.editText?.setText(room.displayName)
        binding.tilTopic.editText?.setText(room.topic)
    }

    private fun onRoomDataChanged() {
        viewModel.handleRoomDataUpdate(
            binding.tilName.editText?.text?.toString()?.trim() ?: "",
            binding.tilTopic.editText?.text?.toString()?.trim() ?: ""
        )
    }
}