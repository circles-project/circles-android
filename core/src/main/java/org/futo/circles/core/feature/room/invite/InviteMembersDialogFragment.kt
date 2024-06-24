package org.futo.circles.core.feature.room.invite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentInviteMembersBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.select_users.SelectUsersFragment
import org.futo.circles.core.feature.select_users.SelectUsersListener
import org.futo.circles.core.model.MessageLoadingData
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.view.LoadingDialog

@AndroidEntryPoint
class InviteMembersDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentInviteMembersBinding>(
        DialogFragmentInviteMembersBinding::inflate
    ), SelectUsersListener,
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<InviteMembersViewModel>()
    private val inviteLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val selectedUsersFragment by lazy { SelectUsersFragment.create(args.roomId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSelectUsersFragment()
        setupObservers()
        binding.btnInvite.setOnClickListener {
            viewModel.invite(selectedUsersFragment.getSelectedUsersIds())
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
            binding.toolbar.title = requireContext().getString(
                R.string.invite_to_format, it
            )
        }
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


    override fun onUserSelected(usersIds: List<String>) {
        binding.btnInvite.isEnabled = usersIds.isNotEmpty()
    }
}