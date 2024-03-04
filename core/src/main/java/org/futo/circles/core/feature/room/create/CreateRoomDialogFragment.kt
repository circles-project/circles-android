package org.futo.circles.core.feature.room.create

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.BundleCompat.getParcelable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentCreateRoomBinding
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.feature.select_users.SelectUsersFragment
import org.futo.circles.core.model.CircleRoomTypeArg

@AndroidEntryPoint
class CreateRoomDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreateRoomBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<CreateRoomViewModel>()
    private val binding by lazy {
        getBinding() as DialogFragmentCreateRoomBinding
    }

    private val mediaPickerHelper = MediaPickerHelper(this)
    private val roomType: CircleRoomTypeArg by lazy {
        getParcelable(requireArguments(), "type", CircleRoomTypeArg::class.java)
            ?: CircleRoomTypeArg.Group
    }
    private var selectedUsersFragment: SelectUsersFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) addSelectUsersFragment()
        setupObservers()
    }

    private fun changeCoverImage() {
        mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    private fun createRoom(
        type: CircleRoomTypeArg,
        name: String,
        topic: String? = null,
        isPublicCircle: Boolean = false
    ) {
        viewModel.createRoom(
            name,
            topic ?: "",
            selectedUsersFragment?.getSelectedUsersIds(),
            type,
            isPublicCircle
        )
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
        }
        viewModel.createRoomResponseLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun addSelectUsersFragment() {
        selectedUsersFragment = SelectUsersFragment.create(null).also {
            childFragmentManager.beginTransaction()
                .replace(R.id.lContainer, it)
                .commitAllowingStateLoss()
        }
    }

    private fun setupViewsCircle() {
        with(binding) {
            ivCover.setOnClickListener { changeCoverImage() }
            tilName.editText?.doAfterTextChanged {
                it?.let { btnCreate.isEnabled = it.isNotEmpty() }
            }
            btnCreate.setOnClickListener {
                createRoom(
                    CircleRoomTypeArg.Circle,
                    tilName.getText(),
                    null,
                    binding.btnPublic.isChecked
                )
                startLoading(btnCreate)
            }
        }
    }

    private fun setupViewsGroup() {
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

    private fun setupViewsGallery() {
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