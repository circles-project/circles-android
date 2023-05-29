package org.futo.circles.feature.groups.update

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.databinding.DialogFragmentUpdateGroupBinding
import org.futo.circles.feature.room.update.UpdateRoomDialogFragment
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class UpdateGroupDialogFragment :
    UpdateRoomDialogFragment(DialogFragmentUpdateGroupBinding::inflate) {

    private val args: UpdateGroupDialogFragmentArgs by navArgs()
    override val roomId: String get() = args.roomId
    override val fragment: Fragment = this
    override val mediaPickerHelper: MediaPickerHelper = MediaPickerHelper(this)
    override val successMessageResId: Int = R.string.group_updated

    private val binding by lazy {
        getBinding() as DialogFragmentUpdateGroupBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onCoverImageSelected(uri: Uri) {
        binding.ivCover.setImageURI(uri)
        onInputDataChanged()
    }

    override fun setInitialGroupData(room: RoomSummary) {
        binding.ivCover.loadProfileIcon(room.avatarUrl, room.displayName)
        binding.tilName.editText?.setText(room.displayName)
        binding.tilTopic.editText?.setText(room.topic)
    }

    override fun setUpdateButtonEnabled(isEnabled: Boolean) {
        binding.btnSave.isEnabled = isEnabled
    }

    private fun setupViews() {
        with(binding) {
            ivCover.setOnClickListener { changeCoverImage() }
            btnChangeIcon.setOnClickListener { changeCoverImage() }
            tilName.editText?.doAfterTextChanged {
                it?.let { onInputDataChanged() }
            }
            tilTopic.editText?.doAfterTextChanged {
                it?.let { onInputDataChanged() }
            }
            btnSave.setOnClickListener {
                updateRoom(tilName.getText(), tilTopic.getText())
                startLoading(btnSave)
            }
        }
    }

    private fun onInputDataChanged() {
        onInputRoomDataChanged(binding.tilName.getText(), binding.tilTopic.getText())
    }
}