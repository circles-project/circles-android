package org.futo.circles.core.room.share

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.model.TextShareable
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.share.ShareProvider
import org.futo.circles.databinding.DialogFragmentShareRoomBinding
import org.matrix.android.sdk.api.session.room.model.RoomSummary

@AndroidEntryPoint
class ShareRoomDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentShareRoomBinding::inflate) {

    private val viewModel by viewModels<ShareRoomViewModel>()
    private val args: ShareRoomDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentShareRoomBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObServers()
    }

    private fun setupViews() {
        binding.toolbar.title =
            getString(if (args.isProfile) R.string.share_profile else R.string.share_room)
        binding.btnShare.setOnClickListener {
            ShareProvider.share(requireContext(), TextShareable(viewModel.buildInviteUrl()))
        }
    }

    private fun setupObServers() {
        viewModel.roomLiveData?.observeData(this) {
            it.getOrNull()?.let { summary ->
                handelQrReady(summary)
            } ?: kotlin.run {
                binding.vLoading.gone()
                showError(getString(R.string.room_not_found))
            }
        }
    }

    private fun handelQrReady(roomSummary: RoomSummary) {
        with(binding) {
            vLoading.gone()
            ivQr.visible()
            ivQr.setData(viewModel.buildInviteUrl())
            tvRoomName.text = if (args.isProfile)
                MatrixSessionProvider.currentSession?.myUserId ?: roomSummary.nameOrId()
            else roomSummary.nameOrId()
            tvRoomId.text = roomSummary.roomId
            btnShare.visible()
        }
    }
}