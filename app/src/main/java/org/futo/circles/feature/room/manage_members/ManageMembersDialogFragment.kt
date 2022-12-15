package org.futo.circles.feature.room.manage_members


import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentManageMembersBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.room.ManageMembersOptionsListener
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelListener
import org.futo.circles.feature.room.manage_members.list.GroupMembersListAdapter
import org.futo.circles.model.ConfirmationType
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent


class ManageMembersDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentManageMembersBinding::inflate),
    ManageMembersOptionsListener, ChangeAccessLevelListener {

    private val args: ManageMembersDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ManageMembersViewModel> {
        parametersOf(args.roomId, args.type)
    }

    private val membersListAdapter by lazy {
        GroupMembersListAdapter(
            this,
            onToggleOptions = { userId -> viewModel.toggleOptionsVisibility(userId) }
        )
    }

    private val binding by lazy {
        getBinding() as DialogFragmentManageMembersBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
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
        withConfirmation(ConfirmationType.REMOVE_ROOM_USER) { viewModel.removeUser(userId) }
    }

    override fun onBanUser(userId: String) {
        withConfirmation(ConfirmationType.BAN_USER) { viewModel.banUser(userId) }
    }

    override fun onChangeAccessLevel(userId: String, levelValue: Int) {
        viewModel.changeAccessLevel(userId, levelValue)
    }

    override fun unBanUser(userId: String) {
        withConfirmation(ConfirmationType.UNBAN_USER) { viewModel.unBanUser(userId) }
    }

    override fun cancelPendingInvitation(userId: String) {
        withConfirmation(ConfirmationType.CANCEL_INVITE) { viewModel.removeUser(userId) }
    }

}