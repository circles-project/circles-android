package com.futo.circles.feature.room.manage_members


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ManageMembersDialogFragmentBinding
import com.futo.circles.extensions.*
import com.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelListener
import com.futo.circles.feature.room.manage_members.list.GroupMembersListAdapter
import com.futo.circles.view.ManageMembersOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent


class ManageMembersDialogFragment :
    BaseFullscreenDialogFragment(ManageMembersDialogFragmentBinding::inflate),
    ManageMembersOptionsListener, ChangeAccessLevelListener {

    private val args: ManageMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ManageMembersViewModel> {
        parametersOf(args.roomId, args.type)
    }

    private val membersListAdapter by lazy {
        GroupMembersListAdapter(
            onToggleOptions = { userId -> viewModel.toggleOptionsVisibility(userId) },
            onCancelInvite = { userId -> showCancelInviteDialog(userId) },
            this
        )
    }

    private val binding by lazy {
        getBinding() as ManageMembersDialogFragmentBinding
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
        viewModel.changeAccessLevelLiveData.observeResponse(this)
    }

    private fun showCancelInviteDialog(userId: String) {
        showDialog(
            titleResIdRes = R.string.cancel_invite,
            messageResId = R.string.cancel_invite_message,
            negativeButtonVisible = true,
            positiveAction = { viewModel.removeUser(userId) }
        )
    }

    override fun onSetAccessLevel(userId: String, powerLevelsContent: PowerLevelsContent) {
        findNavController()
            .navigate(
                ManageMembersDialogFragmentDirections.toChangeAccessLevelBottomSheet(
                    userId = userId,
                    levelValue = powerLevelsContent.getUserPowerLevel(userId),
                    myUserLevelValue = powerLevelsContent.getCurrentUserPowerLevel()
                )
            )
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

    override fun onChangeAccessLevel(userId: String, levelValue: Int) {
        viewModel.changeAccessLevel(userId, levelValue)
    }

}