package com.futo.circles.feature.manage_group_members


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ManageGroupMembersDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.feature.group_invite.InviteMembersDialogFragmentArgs
import com.futo.circles.feature.manage_group_members.list.GroupMembersListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class ManageGroupMembersDialogFragment :
    BaseFullscreenDialogFragment(ManageGroupMembersDialogFragmentBinding::inflate) {

    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ManageGroupMembersViewModel> { parametersOf(args.roomId) }

    private val membersListAdapter by lazy { GroupMembersListAdapter() }

    private val binding by lazy {
        getBinding() as ManageGroupMembersDialogFragmentBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.rvMembers.adapter = membersListAdapter
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.titleLiveData.observeData(this) {
            binding.toolbar.title = it
        }
        viewModel.groupMembersLiveData.observeData(this) {
            membersListAdapter.submitList(it)
        }
    }

}