package com.futo.circles.feature.group_invite

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.base.BaseFullscreenDialogFragment
import com.futo.circles.databinding.InviteMembersDialogFragmentBinding
import com.futo.circles.extensions.*
import com.futo.circles.feature.group_invite.list.search.InviteMembersSearchListAdapter
import com.futo.circles.feature.group_invite.list.selected.SelectedUsersListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class InviteMembersDialogFragment :
    BaseFullscreenDialogFragment(InviteMembersDialogFragmentBinding::inflate) {

    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<InviteMembersViewModel> { parametersOf(args.roomId) }

    private val searchListAdapter by lazy { InviteMembersSearchListAdapter(viewModel::onUserSelected) }
    private val selectedUsersListAdapter by lazy { SelectedUsersListAdapter(viewModel::onUserSelected) }

    private val binding by lazy {
        getBinding() as InviteMembersDialogFragmentBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        setupLists()
        setupObservers()
        binding.btnInvite.setOnClickWithLoading {
            viewModel.invite()
            setLoadingState(true)
        }
    }

    private fun setupLists() {
        binding.rvUsers.adapter = searchListAdapter
        viewModel.initSearchListener(binding.searchView.getQueryTextChangeStateFlow())

        binding.rvSelectedUsers.adapter = selectedUsersListAdapter
    }

    private fun setupObservers() {
        viewModel.titleLiveData.observeData(this) {
            binding.toolbar.title = it
        }
        viewModel.searchUsersLiveData.observeData(this) { items ->
            searchListAdapter.submitList(items)
        }
        viewModel.selectedUsersLiveData.observeData(this) { items ->
            selectedUsersListAdapter.submitList(items)
            binding.selectedUserDivider.setVisibility(items.isNotEmpty())
            binding.btnInvite.setButtonEnabled(items.isNotEmpty())
        }
        viewModel.inviteResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.invitation_sent), true)
                activity?.onBackPressed()
            },
            error = { message -> showError(message) },
            onRequestInvoked = { setLoadingState(false) }
        )
    }

    private fun setLoadingState(isLoading: Boolean) {
        setEnabledViews(!isLoading)
        binding.btnInvite.setIsLoading(isLoading)
    }
}