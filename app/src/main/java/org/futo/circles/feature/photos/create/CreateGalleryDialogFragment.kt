package org.futo.circles.feature.photos.create

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.databinding.DialogFragmentCreateGalleryBinding
import org.futo.circles.core.room.create.CreateRoomDialogFragment

class CreateGalleryDialogFragment :
    CreateRoomDialogFragment(DialogFragmentCreateGalleryBinding::inflate) {

    override val fragment: Fragment = this
    override val inviteContainerId: Int? = null
    override val mediaPickerHelper: MediaPickerHelper = MediaPickerHelper(this)

    private val binding by lazy {
        getBinding() as DialogFragmentCreateGalleryBinding
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
                createRoom(CircleRoomTypeArg.Photo, tilName.getText())
                startLoading(btnCreate)
            }
        }
    }
}