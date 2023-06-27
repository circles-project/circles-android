package org.futo.circles.feature.room.well_known

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentRoomWellKnownBinding
import org.futo.circles.model.RoomPublicInfo
import org.matrix.android.sdk.api.session.room.model.Membership

@AndroidEntryPoint
class RoomWellKnownDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentRoomWellKnownBinding::inflate) {

    private val viewModel by viewModels<RoomWellKnownViewModel>()
    private val args: RoomWellKnownDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentRoomWellKnownBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.tvRoomId.text = args.roomId
        binding.btnRequest.setOnClickListener {
            viewModel.sendKnockRequest()
            binding.btnRequest.setIsLoading(true)
        }
    }

    private fun setupObservers() {
        viewModel.roomPublicInfoLiveData.observeResponse(this,
            success = { roomInfo -> bindRoomData(roomInfo) },
            onRequestInvoked = { binding.vLoading.gone() },
            error = { bindError(it) }
        )
        viewModel.knockRequestLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.request_sent), true)
                onBackPressed()
            },
            onRequestInvoked = { binding.btnRequest.setIsLoading(false) })
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
            tvMembersCount.text = getString(R.string.joined_members_count, roomInfo.memberCount)
            btnRequest.setIsVisible(shouldShowKnockButton(roomInfo.membership))
            tvTopic.setIsVisible(roomInfo.topic?.isNotEmpty() == true)
            tvTopic.text = roomInfo.topic ?: ""
            tvMembersip.text = getString(
                when (roomInfo.membership) {
                    Membership.NONE,
                    Membership.KNOCK,
                    Membership.LEAVE -> R.string.request_to_become_member

                    Membership.INVITE -> R.string.you_have_pending_invitation
                    Membership.JOIN -> R.string.you_are_already_member
                    Membership.BAN -> R.string.you_are_banned
                }
            )
        }
    }

    private fun shouldShowKnockButton(membership: Membership): Boolean =
        membership == Membership.NONE || membership == Membership.KNOCK || membership == Membership.LEAVE


}