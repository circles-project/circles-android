package org.futo.circles.feature.room.well_known

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentRoomWellKnownBinding
import org.futo.circles.model.RoomPublicInfo
import org.matrix.android.sdk.api.session.room.model.Membership

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
            success = { roomInfo -> bindRoomData(roomInfo) },
            onRequestInvoked = { binding.vLoading.gone() },
            error = { bindError(it) }
        )
    }

    private fun bindError(message: String) {
        binding.ivCover.apply {
            setImageResource(R.drawable.ic_error)
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        }
        binding.tvRoomName.text = message
    }

    private fun bindRoomData(roomInfo: RoomPublicInfo) {
        with(binding) {
            ivCover.loadProfileIcon(roomInfo.avatarUrl, roomInfo.displayName)
            toolbar.title = roomInfo.displayName
            tvRoomName.text = roomInfo.displayName
            tvRoomId.text = roomInfo.id
            tvMembersCount.text = getString(R.string.joined_members_count, roomInfo.memberCount)
            btnRequest.setIsVisible(roomInfo.membership == Membership.NONE)
            tvTopic.setIsVisible(roomInfo.topic != null)
            tvTopic.text = roomInfo.topic ?: ""
        }
    }
}