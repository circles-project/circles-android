package org.futo.circles.feature.room.create

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.feature.room.select_users.SelectUsersFragment
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class CreateRoomDialogFragment(inflate: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding) :
    BaseFullscreenDialogFragment(inflate), HasLoadingState {

    abstract val inviteContainerId: Int?
    abstract val mediaPickerHelper: MediaPickerHelper
    private val viewModel by viewModel<CreateRoomViewModel>()
    private var selectedUsersFragment: SelectUsersFragment? = null

    abstract fun onCoverImageSelected(uri: Uri)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSelectUsersFragment(inviteContainerId ?: return)
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
        isKnockingAllowed: Boolean = false
    ) {
        viewModel.createRoom(
            name,
            topic ?: "",
            selectedUsersFragment?.getSelectedUsersIds(),
            type,
            isKnockingAllowed
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