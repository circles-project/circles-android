package com.futo.circles.feature.group_invite

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.base.BaseFullscreenDialogFragment
import com.futo.circles.databinding.InviteMembersDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.feature.group_invite.list.InviteMembersListAdapter
import com.futo.circles.model.RoomMemberListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class InviteMembersDialogFragment :
    BaseFullscreenDialogFragment(InviteMembersDialogFragmentBinding::inflate) {

    private val args: InviteMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<InviteMembersViewModel> { parametersOf(args.roomId) }

    private val listAdapter by lazy { InviteMembersListAdapter() }

    private val binding by lazy {
        getBinding() as InviteMembersDialogFragmentBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_Circles)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.rvUsers.adapter = listAdapter
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.titleLiveData.observeData(this) {
            binding.toolbar.title = it
        }
    }

    private fun setUserList(users: List<RoomMemberListItem>) {
        listAdapter.submitList(users)
    }

}