package org.futo.circles.feature.room.well_known

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentRoomWellKnownBinding
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.UserPublicInfo
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
        viewModel.userPublicInfoLiveData.observeResponse(this,
            success = { userInfo -> bindUserData(userInfo) },
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
            ivCover.loadProfileIcon(url, name)
            toolbar.title = name
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
                text = getString(R.string.joined_members_count, roomInfo.memberCount)
            }
            btnRequest.setText(getString(R.string.requested_to_join))
            tvTopic.setIsVisible(roomInfo.topic?.isNotEmpty() == true)
            tvTopic.text = roomInfo.topic ?: ""
            tvMembersip.text = getString(
                when (roomInfo.membership) {
                    Membership.NONE,
                    Membership.KNOCK,
                    Membership.LEAVE -> R.string.request_to_become_member_room

                    Membership.INVITE -> R.string.you_have_pending_invitation_room
                    Membership.JOIN -> R.string.you_are_already_member_room
                    Membership.BAN -> R.string.you_are_banned_room
                }
            )
        }
    }

    private fun bindUserData(userInfo: UserPublicInfo) {
        with(binding) {
            bindGeneralData(
                userInfo.avatarUrl,
                userInfo.displayName,
                userInfo.membership
            )
            binding.tvRoomId.text = userInfo.id
            tvMembersCount.apply {
                setIsVisible(userInfo.memberCount > 0)
                text = getString(R.string.following_format, userInfo.memberCount)
            }
            btnRequest.setText(getString(R.string.follow))
            tvTopic.gone()
            tvMembersip.text = getString(
                when (userInfo.membership) {
                    Membership.NONE,
                    Membership.KNOCK,
                    Membership.LEAVE -> R.string.send_request_to_follow_user

                    Membership.INVITE -> R.string.you_have_pending_invitation_user
                    Membership.JOIN -> R.string.you_are_already_following_user
                    Membership.BAN -> R.string.you_are_banned_user
                }
            )
        }
    }

    private fun shouldShowKnockButton(membership: Membership): Boolean =
        membership == Membership.NONE || membership == Membership.KNOCK || membership == Membership.LEAVE


}