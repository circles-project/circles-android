package org.futo.circles.core.room.manage_members.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.model.BannedMemberListItem
import org.futo.circles.core.model.GroupMemberListItem
import org.futo.circles.core.model.InvitedMemberListItem
import org.futo.circles.core.model.ManageMembersHeaderListItem
import org.futo.circles.core.model.ManageMembersListItem
import org.futo.circles.core.room.manage_members.ManageMembersOptionsListener

private enum class ManageGroupMembersViewTypes { Header, Member, Invited, Banned }

class GroupMembersListAdapter(
    private val manageMembersListener: ManageMembersOptionsListener,
    private val onToggleOptions: (String) -> Unit
) : BaseRvAdapter<ManageMembersListItem, ManageMembersViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ManageMembersHeaderListItem -> ManageGroupMembersViewTypes.Header.ordinal
        is GroupMemberListItem -> ManageGroupMembersViewTypes.Member.ordinal
        is InvitedMemberListItem -> ManageGroupMembersViewTypes.Invited.ordinal
        is BannedMemberListItem -> ManageGroupMembersViewTypes.Banned.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageMembersViewHolder {
        return when (ManageGroupMembersViewTypes.values()[viewType]) {
            ManageGroupMembersViewTypes.Header -> ManageMembersHeaderViewHolder(parent)
            ManageGroupMembersViewTypes.Member -> MemberViewHolder(
                parent = parent,
                onUserClicked = { position -> onToggleOptions(getItem(position).id) },
                manageMembersListener = manageMembersListener
            )

            ManageGroupMembersViewTypes.Invited -> InvitedMemberViewHolder(
                parent = parent,
                onUserClicked = { position -> onToggleOptions(getItem(position).id) },
                manageMembersListener = manageMembersListener
            )

            ManageGroupMembersViewTypes.Banned -> BannedMemberViewHolder(
                parent, manageMembersListener,
            )
        }
    }

    override fun onBindViewHolder(holder: ManageMembersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}