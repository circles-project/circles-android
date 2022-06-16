package org.futo.circles.feature.room.update_room

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.image_picker.ImagePickerHelper
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.UpdateRoomDialogFragmentBinding
import org.futo.circles.extensions.*
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class UpdateRoomDialogFragment :
    BaseFullscreenDialogFragment(UpdateRoomDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: UpdateRoomDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<UpdateRoomViewModel> { parametersOf(args.roomId) }
    private val imagePickerHelper = ImagePickerHelper(this)

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
            toolbar.title = getTitle()
            tvNameHeader.text = getNameHeader()
            tvEncryptionWarning.text = getEncryptionWarning()
            topicViewGroup.setIsVisible(args.type == CircleRoomTypeArg.Group)
            ivCover.setOnClickListener { showImagePicker() }
            btnChangeIcon.setOnClickListener { showImagePicker() }
            tilName.editText?.doAfterTextChanged {
                it?.let { onRoomDataChanged() }
            }
            tilTopic.editText?.doAfterTextChanged {
                it?.let { onRoomDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.update(tilName.getText(), tilTopic.getText())
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
                showSuccess(getSuccessMessage(), true)
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
        viewModel.handleRoomDataUpdate(binding.tilName.getText(), binding.tilTopic.getText())
    }

    private fun getTitle() = getString(
        when (args.type) {
            CircleRoomTypeArg.Circle -> R.string.configure_circle
            CircleRoomTypeArg.Group -> R.string.configure_group
            CircleRoomTypeArg.Photo -> R.string.configure_gallery
        }
    )

    private fun getNameHeader() = getString(
        when (args.type) {
            CircleRoomTypeArg.Circle -> R.string.circle_name
            CircleRoomTypeArg.Group -> R.string.group_name
            CircleRoomTypeArg.Photo -> R.string.gallery_name
        }
    )

    private fun getEncryptionWarning() = getString(
        when (args.type) {
            CircleRoomTypeArg.Circle -> R.string.circle_encryption_warning
            CircleRoomTypeArg.Group -> R.string.group_encryption_warning
            CircleRoomTypeArg.Photo -> R.string.gallery_encryption_warning
        }
    )

    private fun getSuccessMessage() = getString(
        when (args.type) {
            CircleRoomTypeArg.Circle -> R.string.circle_updated
            CircleRoomTypeArg.Group -> R.string.group_updated
            CircleRoomTypeArg.Photo -> R.string.gallery_updated
        }
    )
}