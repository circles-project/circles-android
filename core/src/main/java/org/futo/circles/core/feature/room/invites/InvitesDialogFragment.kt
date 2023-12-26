package org.futo.circles.core.feature.room.invites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentInvitesBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.feature.room.invites.list.InvitesAdapter
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.InviteListItem
import org.futo.circles.core.model.InviteTypeArg
import org.futo.circles.core.view.EmptyTabPlaceholderView

@AndroidEntryPoint
class InvitesDialogFragment : BaseFullscreenDialogFragment(DialogFragmentInvitesBinding::inflate) {

    private val viewModel by viewModels<InvitesViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentInvitesBinding
    }

    private val invitesAdapter by lazy {
        InvitesAdapter(
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            },
            onUnblurProfileIconClicked = { roomListItem ->
                viewModel.unblurProfileIcon(roomListItem.id)
            }
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
            adapter = invitesAdapter
        }
    }

    private fun setupObservers() {
        viewModel.invitesLiveData.observeData(this) { invitesAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun getTitle(): String = getString(
        when (viewModel.getInviteType()) {
            InviteTypeArg.Circle -> R.string.circle_invitations
            InviteTypeArg.Group -> R.string.group_invitations
            InviteTypeArg.Photo -> R.string.gallery_invitations
            InviteTypeArg.People -> R.string.follow_requests
        }
    )

    private fun getEmptyMessage() = getString(R.string.no_new_invitations_format, getTitle())

    private fun onInviteClicked(item: InviteListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        when (item.inviteType) {
            InviteTypeArg.Circle -> if (isAccepted) onAcceptCircleInviteClicked(item.id)
            else viewModel.rejectRoomInvite(item.id)

            InviteTypeArg.Group -> handleRoomInvite(item.id, isAccepted, CircleRoomTypeArg.Group)
            InviteTypeArg.Photo -> handleRoomInvite(item.id, isAccepted, CircleRoomTypeArg.Photo)
            InviteTypeArg.People -> viewModel.onFollowRequestAnswered(item.id, isAccepted)
        }
    }

    private fun handleRoomInvite(roomId: String, isAccepted: Boolean, type: CircleRoomTypeArg) {
        if (isAccepted) viewModel.acceptRoomInvite(roomId, type)
        else viewModel.rejectRoomInvite(roomId)
    }


    private fun onAcceptCircleInviteClicked(roomId: String) {
        findNavController().navigateSafe(
            InvitesDialogFragmentDirections.toAcceptCircleInviteDialogFragment(roomId)
        )
    }


}