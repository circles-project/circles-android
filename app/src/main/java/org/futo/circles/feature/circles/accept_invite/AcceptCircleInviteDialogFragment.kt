package org.futo.circles.feature.circles.accept_invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.base.RoomsListener
import org.futo.circles.base.SelectRoomsListener
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentAcceptCircleInviteBinding
import org.futo.circles.feature.room.select.SelectRoomsFragment
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AcceptCircleInviteDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentAcceptCircleInviteBinding::inflate),
    HasLoadingState, SelectRoomsListener, RoomsListener {

    override val fragment: Fragment = this
    private val args: AcceptCircleInviteDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<AcceptCircleInviteViewModel> {
        parametersOf(args.roomId)
    }
    private val binding by lazy {
        getBinding() as DialogFragmentAcceptCircleInviteBinding
    }

    private val selectRoomsFragment by lazy { SelectRoomsFragment.create(CircleRoomTypeArg.Circle) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSelectCirclesFragment()
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
            btnInvite.setOnClickListener {
                viewModel.acceptInvite(selectRoomsFragment.getSelectedRooms())
                startLoading(btnInvite)
            }
            fbAddRoom.setOnClickListener { navigateToCreateCircle() }
            btnCreateCircle.setOnClickListener { navigateToCreateCircle() }
        }
    }

    private fun setupObservers() {
        viewModel.acceptResultLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun navigateToCreateCircle() {
        findNavController()
            .navigate(AcceptCircleInviteDialogFragmentDirections.toCreateCircleDialogFragment())
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnInvite.isEnabled = rooms.isNotEmpty()
    }

    override fun onRoomsListChanged(rooms: List<SelectableRoomListItem>) {
        binding.createCircleGroup.setIsVisible(rooms.isEmpty())
        binding.selectCircleGroup.setIsVisible(!rooms.isEmpty())
    }
}