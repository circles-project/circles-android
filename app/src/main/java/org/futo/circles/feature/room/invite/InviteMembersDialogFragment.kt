package org.futo.circles.feature.room.invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.InviteMembersDialogFragmentBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showSuccess
import org.futo.circles.feature.room.select_users.SelectUsersFragment
import org.futo.circles.feature.room.select_users.SelectUsersListener
import org.futo.circles.model.UserListItem
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