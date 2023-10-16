package org.futo.circles.core.feature.room.create

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.feature.select_users.SelectUsersFragment

abstract class CreateRoomDialogFragment(inflate: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding) :
    BaseFullscreenDialogFragment(inflate), HasLoadingState {

    abstract val inviteContainerId: Int?
    abstract val mediaPickerHelper: MediaPickerHelper
    private val viewModel by viewModels<CreateRoomViewModel>()
    private var selectedUsersFragment: SelectUsersFragment? = null

    abstract fun onCoverImageSelected(uri: Uri)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inviteContainerId?.let { addSelectUsersFragment(it) }
        setupObservers()
    }

    protected fun changeCoverImage() {
        mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    protected fun createRoom(
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
            onCoverImageSelected(it)
        }
        viewModel.createRoomResponseLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun addSelectUsersFragment(containerId: Int) {
        selectedUsersFragment = SelectUsersFragment.create(null).also {
            childFragmentManager.beginTransaction()
                .replace(containerId, it)
                .commitAllowingStateLoss()
        }
    }
}