package com.futo.circles.feature.circles.accept_invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.AcceptCircleInviteDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.feature.circles.accept_invite.list.CirclesInviteAdapter
import com.futo.circles.feature.circles.accept_invite.list.selected.SelectedCirclesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AcceptCircleInviteDialogFragment :
    BaseFullscreenDialogFragment(AcceptCircleInviteDialogFragmentBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: AcceptCircleInviteDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<AcceptCircleInviteViewModel> {
        parametersOf(args.roomId)
    }
    private val binding by lazy {
        getBinding() as AcceptCircleInviteDialogFragmentBinding
    }
    private val circlesInviteAdapter by lazy { CirclesInviteAdapter(viewModel::onCircleSelected) }
    private val selectedCircleAdapter by lazy { SelectedCirclesAdapter(viewModel::onCircleSelected) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            rvCircles.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = circlesInviteAdapter
            }
            rvSelectedCircles.adapter = selectedCircleAdapter
            btnInvite.setOnClickListener {
                viewModel.acceptInvite()
                startLoading(btnInvite)
            }
        }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData.observeData(this) { items ->
            circlesInviteAdapter.submitList(items)
            val selectedCircles = viewModel.getSelectedCircles()
            selectedCircleAdapter.submitList(selectedCircles)
            binding.selectedUserDivider.setIsVisible(selectedCircles.isNotEmpty())
            binding.btnInvite.isEnabled = selectedCircles.isNotEmpty()
        }
    }
}