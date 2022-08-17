package org.futo.circles.feature.circles.accept_invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.SelectRoomsListener
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentAcceptCircleInviteBinding
import org.futo.circles.extensions.observeResponse
import org.futo.circles.feature.circles.select.SelectCirclesFragment
import org.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AcceptCircleInviteDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentAcceptCircleInviteBinding::inflate),
    HasLoadingState, SelectRoomsListener {

    override val fragment: Fragment = this
    private val args: AcceptCircleInviteDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<AcceptCircleInviteViewModel> {
        parametersOf(args.roomId)
    }
    private val binding by lazy {
        getBinding() as DialogFragmentAcceptCircleInviteBinding
    }

    private val selectCirclesFragment by lazy { SelectCirclesFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSelectCirclesFragment()
        setupViews()
        setupObservers()
    }

    private fun addSelectCirclesFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectCirclesFragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            btnInvite.setOnClickListener {
                viewModel.acceptInvite(selectCirclesFragment.getSelectedCircles())
                startLoading(btnInvite)
            }
        }
    }

    private fun setupObservers() {
        viewModel.acceptResultLiveData.observeResponse(this,
            success = { activity?.onBackPressed() }
        )
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnInvite.isEnabled = rooms.isNotEmpty()
    }
}