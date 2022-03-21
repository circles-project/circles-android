package com.futo.circles.feature.group_invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.BaseFullscreenDialogFragment
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.InviteMembersDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.select_users.SelectUsersFragment
import com.futo.circles.feature.select_users.SelectUsersListener
import com.futo.circles.model.UserListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class InviteMembersDialogFragment :
    BaseFullscreenDialogFragment(InviteMembersDialogFragmentBinding::inflate), SelectUsersListener,
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<InviteMembersViewModel> { parametersOf(args.roomId) }

    private val binding by lazy {
        getBinding() as InviteMembersDialogFragmentBinding
    }

    private val selectedUsersFragment by lazy { SelectUsersFragment.create(args.roomId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        addSelectUsersFragment()
        setupObservers()
        binding.btnInvite.setOnClickListener {
            viewModel.invite(selectedUsersFragment.getSelectedUsers())
            startLoading(binding.btnInvite)
        }
    }

    private fun addSelectUsersFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectedUsersFragment)
            .commitAllowingStateLoss()
    }

    private fun setupObservers() {
        viewModel.titleLiveData.observeData(this) {
            binding.toolbar.title = it
        }
        viewModel.inviteResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.invitation_sent), true)
                activity?.onBackPressed()
            }
        )
    }


    override fun onUserSelected(users: List<UserListItem>) {
        binding.btnInvite.isEnabled = users.isNotEmpty()
    }
}