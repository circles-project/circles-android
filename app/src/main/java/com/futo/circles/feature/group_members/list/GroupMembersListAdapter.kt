package com.futo.circles.feature.group_members.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.model.InvitedUserListItem
import com.futo.circles.model.ManageMembersHeaderListItem
import com.futo.circles.model.ManageMembersListItem
import com.futo.circles.view.ManageMembersOptionsListener

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
            ManageGroupMembersViewTypes.Member -> GroupMemberViewHolder(
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