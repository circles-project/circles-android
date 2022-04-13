package com.futo.circles.feature.manage_group_members


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ManageGroupMembersDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.feature.group_invite.InviteMembersDialogFragmentArgs
import com.futo.circles.feature.manage_group_members.list.GroupMembersListAdapter
import com.futo.circles.view.ManageMembersOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class ManageGroupMembersDialogFragment :
    BaseFullscreenDialogFragment(ManageGroupMembersDialogFragmentBinding::inflate) {

    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ManageGroupMembersViewModel> { parametersOf(args.roomId) }

    private val membersListAdapter by lazy {
        GroupMembersListAdapter(
            onToggleOptions = { userId -> viewModel.toggleOptionsVisibility(userId) },
            object : ManageMembersOptionsListener {
                override fun onSetAccessLevel(userId: String) {

                }

                override fun onRemoveUser(userId: String) {

                }

                override fun onBanUser(userId: String) {

                }

            })
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
    }

}