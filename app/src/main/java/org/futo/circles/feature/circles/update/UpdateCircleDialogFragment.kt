package org.futo.circles.feature.circles.update

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.room.update.UpdateRoomDialogFragment
import org.futo.circles.core.utils.isCircleShared
import org.futo.circles.databinding.DialogFragmentUpdateCircleBinding
import org.futo.circles.gallery.feature.pick.AllMediaPickerHelper
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class UpdateCircleDialogFragment :
    UpdateRoomDialogFragment(DialogFragmentUpdateCircleBinding::inflate) {

    private val args: UpdateCircleDialogFragmentArgs by navArgs()
    override val roomId: String get() = args.roomId
    override val fragment: Fragment = this
    override val mediaPickerHelper = AllMediaPickerHelper(this)
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
        val isCircleShared = isCircleShared(roomId)
        binding.btnPrivate.isChecked = !isCircleShared
        binding.btnPublic.isChecked = isCircleShared
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
            binding.circleTypeGroup.setOnCheckedChangeListener { _, _ ->
                onInputDataChanged()
            }
            btnSave.setOnClickListener {
                updateRoom(tilName.getText(), null, isPublicRulesSelected())
                startLoading(btnSave)
            }
        }
    }

    private fun isPublicRulesSelected(): Boolean {
        val checkedId = binding.circleTypeGroup.checkedRadioButtonId
        return checkedId == binding.btnPublic.id

    }

    private fun onInputDataChanged() {
        onInputRoomDataChanged(binding.tilName.getText(), null, isPublicRulesSelected())
    }
}