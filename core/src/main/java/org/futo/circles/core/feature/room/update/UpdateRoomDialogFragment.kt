package org.futo.circles.core.feature.room.update

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentUpdateRoomBinding
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.model.CircleRoomTypeArg
import org.matrix.android.sdk.api.session.room.model.RoomSummary

@AndroidEntryPoint
class UpdateRoomDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentUpdateRoomBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: UpdateRoomDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<UpdateRoomViewModel>()
    private val roomId: String get() = args.roomId
    private val roomType: CircleRoomTypeArg get() = args.type
    private val mediaPickerHelper = MediaPickerHelper(this)

    private val binding by lazy {
        getBinding() as DialogFragmentUpdateRoomBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            ivCover.setOnClickListener { changeCoverImage() }
            btnChangeIcon.setOnClickListener { changeCoverImage() }
            tilName.editText?.doAfterTextChanged {
                it?.let { onInputDataChanged() }
            }
            binding.circleTypeGroup.setOnCheckedChangeListener { _, _ ->
                onInputDataChanged()
            }
            tilTopic.editText?.doAfterTextChanged {
                it?.let { onInputDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.update(tilName.getText(), tilTopic.getText(), isPublicRulesSelected())
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
            onInputDataChanged()
        }
        viewModel.updateGroupResponseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.updated))
                onBackPressed()
            }
        )
        viewModel.groupSummaryLiveData.observeData(this) {
            it?.let { setInitialRoomData(it) }
        }
        viewModel.isRoomDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
    }

    private fun setInitialRoomData(room: RoomSummary) {
        binding.ivCover.loadRoomProfileIcon(room.avatarUrl, room.displayName)
        binding.tilName.editText?.setText(room.displayName)
        binding.tilTopic.editText?.setText(room.topic)
        val isCircleShared = viewModel.isCircleShared(roomId)
        binding.btnPrivate.isChecked = !isCircleShared
        binding.btnPublic.isChecked = isCircleShared
    }

    private fun isPublicRulesSelected(): Boolean {
        val checkedId = binding.circleTypeGroup.checkedRadioButtonId
        return checkedId == binding.btnPublic.id
    }

    private fun changeCoverImage() {
        mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    private fun onInputDataChanged() {
        viewModel.handleRoomDataUpdate(
            binding.tilName.getText(),
            binding.tilTopic.getText(),
            isPublicRulesSelected()
        )
    }

}