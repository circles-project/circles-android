package org.futo.circles.feature.groups.create


import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.databinding.DialogFragmentCreateGroupBinding
import org.futo.circles.core.room.create.CreateRoomDialogFragment

class CreateGroupDialogFragment :
    CreateRoomDialogFragment(DialogFragmentCreateGroupBinding::inflate) {

    override val fragment: Fragment = this
    override val inviteContainerId: Int = R.id.lContainer
    override val mediaPickerHelper: MediaPickerHelper = MediaPickerHelper(this)

    private val binding by lazy {
        getBinding() as DialogFragmentCreateGroupBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onCoverImageSelected(uri: Uri) {
        binding.ivCover.setImageURI(uri)
    }

    private fun setupViews() {
        with(binding) {
            ivCover.setOnClickListener { changeCoverImage() }
            tilName.editText?.doAfterTextChanged {
                it?.let { btnCreate.isEnabled = it.isNotEmpty() }
            }
            btnCreate.setOnClickListener {
                createRoom(CircleRoomTypeArg.Group, tilName.getText(), tilTopic.getText())
                startLoading(btnCreate)
            }
        }
    }
}