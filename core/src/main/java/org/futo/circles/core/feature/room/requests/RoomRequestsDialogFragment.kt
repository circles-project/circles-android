package org.futo.circles.core.feature.room.requests

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentRoomRequestsBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.feature.room.requests.list.RoomRequestsAdapter
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestTypeArg
import org.futo.circles.core.view.EmptyTabPlaceholderView

@AndroidEntryPoint
class RoomRequestsDialogFragment : BaseFullscreenDialogFragment<DialogFragmentRoomRequestsBinding>(
    DialogFragmentRoomRequestsBinding::inflate
) {

    private val args: RoomRequestsDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<RoomRequestsViewModel>()

    private val roomRequestsAdapter by lazy {
        RoomRequestsAdapter(
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            },
            onKnockClicked = { item, isAccepted -> onKnockRequestClicked(item, isAccepted) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.title = getTitle()
        binding.rvInvites.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getEmptyMessage())
            })
            adapter = roomRequestsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.requestsLiveData.observeData(this) {
            roomRequestsAdapter.submitList(it)
        }
        viewModel.requestResultLiveData.observeResponse(this)
    }

    private fun isOnlyKnockRequests() = args.roomId != null

    private fun getTitle(): String {
        val roomTypeName = getString(
            when (args.type) {
                RoomRequestTypeArg.Circle -> R.string.circle
                RoomRequestTypeArg.Group -> R.string.group
                RoomRequestTypeArg.Photo -> R.string.gallery
                RoomRequestTypeArg.DM -> return getString(R.string.direct_messages_invitations)
            }
        )
        return getString(
            if (isOnlyKnockRequests()) R.string.room_requests_for_invitation_format
            else R.string.room_invitations_and_requests_format, roomTypeName
        )
    }

    private fun getEmptyMessage() = getString(R.string.no_new_invitations_format, getTitle())

    private fun onInviteClicked(item: RoomInviteListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        handleRoomInvite(item.id, isAccepted, item.requestType)
    }

    private fun handleRoomInvite(roomId: String, isAccepted: Boolean, type: RoomRequestTypeArg) {
        if (isAccepted) viewModel.acceptRoomInvite(roomId, type)
        else viewModel.rejectRoomInvite(roomId)
    }


    private fun onKnockRequestClicked(user: KnockRequestListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        if (isAccepted) viewModel.inviteUser(user)
        else viewModel.kickUser(user)
    }

}