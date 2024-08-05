package org.futo.circles.core.feature.invite_to_follow


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentInviteToFollowMeBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.room.select.SelectRoomsFragment
import org.futo.circles.core.feature.room.select.interfaces.RoomsListener
import org.futo.circles.core.feature.room.select.interfaces.SelectRoomsListener
import org.futo.circles.core.model.MessageLoadingData
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.model.SelectRoomTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.view.LoadingDialog

@AndroidEntryPoint
class InviteToFollowMeDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentInviteToFollowMeBinding>(
        DialogFragmentInviteToFollowMeBinding::inflate
    ), HasLoadingState, SelectRoomsListener, RoomsListener {

    override val fragment: Fragment = this
    private val args: InviteToFollowMeDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<InviteToFollowMeViewModel>()
    private val inviteLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val selectRoomsFragment by lazy {
        SelectRoomsFragment.create(SelectRoomTypeArg.MyCirclesNotJoinedByUser, args.userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) addSelectCirclesFragment()
        setupViews()
        setupObservers()
    }

    private fun addSelectCirclesFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectRoomsFragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        with(binding) {
            tvTitle.text =
                getString(R.string.select_circles_to_which_you_want_to_invite_format, args.userId)
            btnInvite.setOnClickListener {
                viewModel.invite(args.userId, selectRoomsFragment.getSelectedRooms())
                startLoading(btnInvite)
            }
            fbAddRoom.setOnClickListener { navigateToCreateCircle() }
            btnCreateCircle.setOnClickListener { navigateToCreateCircle() }
        }
    }

    private fun setupObservers() {
        viewModel.inviteResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.invitation_sent))
                onBackPressed()
            }
        )
        viewModel.inviteLoadingEventLiveData.observeData(this) { event ->
            val loadingData = if (event.isLoading) {
                MessageLoadingData(
                    getString(R.string.inviting_user_to_format, event.userId, event.roomName)
                )
            } else {
                ResLoadingData(isLoading = false)
            }
            inviteLoadingDialog.handleLoading(loadingData)
        }
    }

    private fun navigateToCreateCircle() {
        findNavController().navigateSafe(InviteToFollowMeDialogFragmentDirections.toCreateRoomNavGraph())
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnInvite.isEnabled = rooms.isNotEmpty()
    }

    override fun onRoomsListChanged(rooms: List<SelectableRoomListItem>) {
        binding.createCircleGroup.setIsVisible(rooms.isEmpty())
        binding.selectCircleGroup.setIsVisible(rooms.isNotEmpty())
    }
}