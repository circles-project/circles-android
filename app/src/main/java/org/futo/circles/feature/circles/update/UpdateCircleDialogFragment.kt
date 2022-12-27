package org.futo.circles.feature.circles.update

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.databinding.DialogFragmentUpdateCircleBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.feature.room.update.UpdateRoomDialogFragment
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class UpdateCircleDialogFragment :
    UpdateRoomDialogFragment(DialogFragmentUpdateCircleBinding::inflate) {

    private val args: UpdateCircleDialogFragmentArgs by navArgs()
    override val roomId: String get() = args.roomId
    override val fragment: Fragment = this
    override val mediaPickerHelper: MediaPickerHelper = MediaPickerHelper(this)
    override val successMessageResId: Int = R.string.circle_updated

    private val binding by lazy {
        getBinding() as DialogFragmentUpdateCircleBinding
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
        binding.btnPrivate.isChecked = room.joinRules == RoomJoinRules.INVITE
        binding.btnPublic.isChecked = room.joinRules == RoomJoinRules.KNOCK
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
            btnSave.setOnClickListener {
                updateRoom(tilName.getText())
                startLoading(btnSave)
            }
        }
    }

    private fun onInputDataChanged() {
        onInputRoomDataChanged(binding.tilName.getText())
    }
}