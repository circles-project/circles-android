package com.futo.circles.feature.manage_group_members


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ManageGroupMembersDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showDialog
import com.futo.circles.feature.group_invite.InviteMembersDialogFragmentArgs
import com.futo.circles.feature.manage_group_members.list.GroupMembersListAdapter
import com.futo.circles.view.ManageMembersOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class ManageGroupMembersDialogFragment :
    BaseFullscreenDialogFragment(ManageGroupMembersDialogFragmentBinding::inflate),
    ManageMembersOptionsListener {

    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ManageGroupMembersViewModel> { parametersOf(args.roomId) }

    private val membersListAdapter by lazy {
        GroupMembersListAdapter(
            onToggleOptions = { userId -> viewModel.toggleOptionsVisibility(userId) },
            onCancelInvite = { userId -> showCancelInviteDialog(userId) },
            this
        )
    }

    private val binding by lazy {
        getBinding() as ManageGroupMembersDialogFragmentBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.rvMembers.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = membersListAdapter
        }
    }

    private fun setupObservers() {
        viewModel.titleLiveData.observeData(this) {
            binding.toolbar.title = it
        }
        viewModel.groupMembersLiveData.observeData(this) {
            membersListAdapter.submitList(it)
        }
        viewModel.removeUserResultLiveData.observeResponse(this)
        viewModel.banUserResultLiveData.observeResponse(this)
    }

    private fun showCancelInviteDialog(userId: String) {
        showDialog(
            titleResIdRes = R.string.cancel_invite,
            messageResId = R.string.cancel_invite_message,
            negativeButtonVisible = true,
            positiveAction = { viewModel.removeUser(userId) }
        )
    }

    override fun onSetAccessLevel(userId: String) {

    }

    override fun onRemoveUser(userId: String) {
        showDialog(
            titleResIdRes = R.string.remove_user,
            messageResId = R.string.remove_user_message,
            positiveButtonRes = R.string.remove,
            negativeButtonVisible = true,
            positiveAction = { viewModel.removeUser(userId) }
        )
    }

    override fun onBanUser(userId: String) {
        showDialog(
            titleResIdRes = R.string.ban_user,
            messageResId = R.string.ban_user_message,
            positiveButtonRes = R.string.ban,
            negativeButtonVisible = true,
            positiveAction = { viewModel.banUser(userId) }
        )
    }

}