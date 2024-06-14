package org.futo.circles.core.feature.room.requests

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentRoomRequestsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.feature.room.requests.list.RoomRequestsAdapter
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.view.EmptyTabPlaceholderView

@AndroidEntryPoint
class RoomRequestsDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentRoomRequestsBinding>(
        DialogFragmentRoomRequestsBinding::inflate
    ) {

    private val viewModel by viewModels<RoomRequestsViewModel>()

    private val roomRequestsAdapter by lazy {
        RoomRequestsAdapter(
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            },
            onUnblurProfileIconClicked = { roomListItem ->
                viewModel.unblurProfileIcon(roomListItem.id)
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
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = roomRequestsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.invitesLiveData.observeData(this) { roomRequestsAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun getTitle(): String = getString(
        when (viewModel.getInviteType()) {
            CircleRoomTypeArg.Circle -> R.string.circle_invitations
            CircleRoomTypeArg.Group -> R.string.group_invitations
            CircleRoomTypeArg.Photo -> R.string.gallery_invitations
        }
    )

    private fun getEmptyMessage() = getString(R.string.no_new_invitations_format, getTitle())

    private fun onInviteClicked(item: RoomInviteListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        when (item.roomType) {
            CircleRoomTypeArg.Circle -> handleCircleInvite(item.id, isAccepted)
            else -> handleRoomInvite(item.id, isAccepted, item.roomType)
        }
    }

    private fun handleCircleInvite(roomId: String, isAccepted: Boolean) {
        if (isAccepted) onAcceptCircleInviteClicked(roomId)
        else viewModel.rejectRoomInvite(roomId)
    }

    private fun handleRoomInvite(roomId: String, isAccepted: Boolean, type: CircleRoomTypeArg) {
        if (isAccepted) viewModel.acceptRoomInvite(roomId, type)
        else viewModel.rejectRoomInvite(roomId)
    }


    private fun onAcceptCircleInviteClicked(roomId: String) {
        findNavController().navigateSafe(
            RoomRequestsDialogFragmentDirections.toAcceptCircleInviteDialogFragment(roomId)
        )
    }

    private fun onKnockRequestClicked(user: KnockRequestListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        if (isAccepted) viewModel.inviteUser(user)
        else viewModel.kickUser(user)
    }

}