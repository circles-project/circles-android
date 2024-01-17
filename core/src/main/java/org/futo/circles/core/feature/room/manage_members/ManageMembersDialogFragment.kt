package org.futo.circles.core.feature.room.manage_members


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentManageMembersBinding
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.extensions.getUserPowerLevel
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.room.manage_members.change_role.ChangeAccessLevelListener
import org.futo.circles.core.feature.room.manage_members.list.GroupMembersListAdapter
import org.futo.circles.core.model.BanUser
import org.futo.circles.core.model.CancelInvite
import org.futo.circles.core.model.RemoveRoomUser
import org.futo.circles.core.model.ResendInvite
import org.futo.circles.core.model.UnbanUser
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

@AndroidEntryPoint
class ManageMembersDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentManageMembersBinding::inflate),
    ManageMembersOptionsListener, ChangeAccessLevelListener {

    private val viewModel by viewModels<ManageMembersViewModel>()

    private val membersListAdapter by lazy {
        GroupMembersListAdapter(
            this,
            onToggleOptions = { userId -> viewModel.toggleOptionsVisibility(userId) },
            onOpenUserPage = {
                if (it == MatrixSessionProvider.currentSession?.myUserId) return@GroupMembersListAdapter
                findNavController().navigateSafe(
                    ManageMembersDialogFragmentDirections.toUserDialogFragment(it)
                )
            }
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
        viewModel.responseLiveData.observeResponse(this)
    }


    override fun onSetAccessLevel(userId: String, powerLevelsContent: PowerLevelsContent) {
        findNavController()
            .navigateSafe(
                ManageMembersDialogFragmentDirections.toChangeAccessLevelBottomSheet(
                    userId = userId,
                    levelValue = powerLevelsContent.getUserPowerLevel(userId),
                    myUserLevelValue = powerLevelsContent.getCurrentUserPowerLevel()
                )
            )
    }

    override fun onRemoveUser(userId: String) {
        withConfirmation(RemoveRoomUser()) { viewModel.removeUser(userId) }
    }

    override fun onBanUser(userId: String) {
        withConfirmation(BanUser()) { viewModel.banUser(userId) }
    }

    override fun onChangeAccessLevel(userId: String, levelValue: Int) {
        viewModel.changeAccessLevel(userId, levelValue)
    }

    override fun unBanUser(userId: String) {
        withConfirmation(UnbanUser()) { viewModel.unBanUser(userId) }
    }

    override fun cancelPendingInvitation(userId: String) {
        withConfirmation(CancelInvite()) { viewModel.removeUser(userId) }
    }

    override fun resendInvitation(userId: String) {
        withConfirmation(ResendInvite()) { viewModel.resendInvitation(userId) }
    }

}