package org.futo.circles.feature.room.well_known

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.databinding.DialogFragmentRoomWellKnownBinding
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.isProfile
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
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
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
                showSuccess(getString(R.string.request_sent))
                onBackPressed()
            },
            onRequestInvoked = { binding.btnRequest.setIsLoading(false) })

        viewModel.parseErrorEventLiveData.observeData(this) {
            binding.vLoading.gone()
            bindError(getString(R.string.unable_to_parse_url))
        }
    }

    private fun bindError(message: String) {
        binding.ivCover.apply {
            setImageResource(R.drawable.ic_error)
            setColorFilter(ContextCompat.getColor(requireContext(), R.color.red))
        }
        binding.tvRoomName.text = message
    }

    private fun bindGeneralData(
        url: String?,
        name: String,
        membership: Membership
    ) {
        with(binding) {
            ivCover.setIsVisible(!url.isNullOrBlank())
            url?.let { ivCover.loadProfileIcon(url, name) }
            tvRoomName.text = name
            btnRequest.setIsVisible(shouldShowKnockButton(membership))
        }
    }

    private fun bindRoomData(roomInfo: RoomPublicInfo) {
        with(binding) {
            bindGeneralData(
                roomInfo.avatarUrl,
                roomInfo.displayName,
                roomInfo.membership
            )
            binding.tvRoomId.text = roomInfo.id
            tvMembersCount.apply {
                setIsVisible(roomInfo.memberCount > 0)
                text = getString(
                    if (roomInfo.isProfile()) R.string.following_format
                    else R.string.joined_members_count, roomInfo.memberCount
                )
            }
            btnRequest.setText(getString(if (roomInfo.isProfile()) R.string.request_to_follow else R.string.request_to_join))
            tvTopic.setIsVisible(roomInfo.topic?.isNotEmpty() == true)
            tvTopic.text = roomInfo.topic ?: ""
            tvMembersip.text = getString(
                when (roomInfo.membership) {
                    Membership.NONE,
                    Membership.KNOCK,
                    Membership.LEAVE -> if (roomInfo.isProfile()) R.string.send_request_to_follow_user
                    else R.string.request_to_become_member_room

                    Membership.INVITE -> if (roomInfo.isProfile()) R.string.you_have_pending_invitation_user
                    else R.string.you_have_pending_invitation_room

                    Membership.JOIN -> if (roomInfo.isProfile()) R.string.you_are_already_following_user
                    else R.string.you_are_already_member_room

                    Membership.BAN -> R.string.you_are_banned_room
                }
            )
        }
    }

    private fun shouldShowKnockButton(membership: Membership): Boolean =
        membership == Membership.NONE || membership == Membership.KNOCK || membership == Membership.LEAVE


}