package org.futo.circles.feature.direct.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.getQueryTextChangeStateFlow
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.DialogFragmentCreateDmBinding
import org.futo.circles.feature.direct.create.list.CreateDMListAdapter

@AndroidEntryPoint
class CreateDMDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentCreateDmBinding>(DialogFragmentCreateDmBinding::inflate) {

    private val viewModel by viewModels<CreateDMViewModel>()
    private val searchListAdapter by lazy {
        CreateDMListAdapter(
            onUserClicked = { userId ->
                findNavController().navigateSafe(
                    CreateDMDialogFragmentDirections.toUserNavGraph(userId)
                )
            },
            onStartDmClicked = { userId -> viewModel.inviteForDirectMessages(userId) }
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }


    private fun setupViews() {
        binding.rvUsers.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.no_results))
            })
            adapter = searchListAdapter
        }
        viewModel.initSearchListener(binding.searchView.getQueryTextChangeStateFlow())
    }

    private fun setupObservers() {
        viewModel.searchUsersLiveData.observeData(this) { items ->
            searchListAdapter.submitList(items)
        }
        viewModel.inviteForDirectMessagesLiveData.observeResponse(
            this,
            success = {
                context?.let { showSuccess(it.getString(org.futo.circles.core.R.string.invitation_sent)) }
            })
    }

}