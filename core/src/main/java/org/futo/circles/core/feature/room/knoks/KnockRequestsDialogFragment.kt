package org.futo.circles.core.feature.room.knoks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentKnockRequestsBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.feature.room.knoks.list.KnockRequestsAdapter
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.view.EmptyTabPlaceholderView

@AndroidEntryPoint
class KnockRequestsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentKnockRequestsBinding::inflate) {

    private val viewModel by viewModels<KnockRequestViewModel>()

    private val knocksListAdapter by lazy {
        KnockRequestsAdapter(
            onRequestClicked = { item, isAccepted -> onRequestClicked(item, isAccepted) }
        )
    }

    private val binding by lazy {
        getBinding() as DialogFragmentKnockRequestsBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvRequests.apply {
            adapter = knocksListAdapter
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.knock_requests_empty_message))
            })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewModel.inviteResultLiveData.observeResponse(this)
        viewModel.knockRequestsLiveData?.observeData(this) {
            knocksListAdapter.submitList(it)
        }
    }

    private fun onRequestClicked(user: KnockRequestListItem, isAccepted: Boolean) {
        if (showNoInternetConnection()) return
        if (isAccepted) viewModel.inviteUser(user)
        else viewModel.kickUser(user)
    }

}