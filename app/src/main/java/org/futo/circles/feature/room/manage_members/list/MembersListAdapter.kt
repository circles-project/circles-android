package org.futo.circles.feature.room.manage_members.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.InvitedUserListItem
import org.futo.circles.model.ManageMembersHeaderListItem
import org.futo.circles.model.ManageMembersListItem
import org.futo.circles.view.ManageMembersOptionsListener

private enum class ManageGroupMembersViewTypes { Header, Member, Invited }

class GroupMembersListAdapter(
    private val onToggleOptions: (String) -> Unit,
    private val onCancelInvite: (String) -> Unit,
    private val manageMembersListener: ManageMembersOptionsListener
) : BaseRvAdapter<ManageMembersListItem, ManageMembersViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ManageMembersHeaderListItem -> ManageGroupMembersViewTypes.Header.ordinal
        is GroupMemberListItem -> ManageGroupMembersViewTypes.Member.ordinal
        is InvitedUserListItem -> ManageGroupMembersViewTypes.Invited.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageMembersViewHolder {
        return when (ManageGroupMembersViewTypes.values()[viewType]) {
            ManageGroupMembersViewTypes.Header -> ManageMembersHeaderViewHolder(parent)
            ManageGroupMembersViewTypes.Member -> MemberViewHolder(
                parent = parent,
                onUserClicked = { position -> onToggleOptions(getItem(position).id) },
                manageMembersListener = manageMembersListener
            )
            ManageGroupMembersViewTypes.Invited -> InvitedUserViewHolder(
                parent= parent,
                onCancelInvitation = { position -> onCancelInvite(getItem(position).id) },
            )
        }
    }

    override fun onBindViewHolder(holder: ManageMembersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}