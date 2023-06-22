package org.futo.circles.feature.room.well_known

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentRoomWellKnownBinding

@AndroidEntryPoint
class RoomWellKnownDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentRoomWellKnownBinding::inflate) {

    private val viewModel by viewModels<RoomWellKnownViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentRoomWellKnownBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.roomPublicInfoLiveData.observeResponse(this,
            success = {
                      showSuccess("sucees")
            },
            error = {
                showError(it)
            }
        )
    }
}