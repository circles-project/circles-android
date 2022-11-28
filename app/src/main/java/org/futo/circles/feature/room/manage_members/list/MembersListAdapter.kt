package org.futo.circles.feature.room.manage_members.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.feature.room.ManageMembersOptionsListener
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.ManageMembersHeaderListItem
import org.futo.circles.model.ManageMembersListItem
import org.futo.circles.model.NotJoinedUserListItem

private enum class ManageGroupMembersViewTypes { Header, Member, NotJoined }

class GroupMembersListAdapter(
    private val manageMembersListener: ManageMembersOptionsListener,
    private val onToggleOptions: (String) -> Unit
) : BaseRvAdapter<ManageMembersListItem, ManageMembersViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ManageMembersHeaderListItem -> ManageGroupMembersViewTypes.Header.ordinal
        is GroupMemberListItem -> ManageGroupMembersViewTypes.Member.ordinal
        is NotJoinedUserListItem -> ManageGroupMembersViewTypes.NotJoined.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageMembersViewHolder {
        return when (ManageGroupMembersViewTypes.values()[viewType]) {
            ManageGroupMembersViewTypes.Header -> ManageMembersHeaderViewHolder(parent)
            ManageGroupMembersViewTypes.Member -> MemberViewHolder(
                parent = parent,
                onUserClicked = { position -> onToggleOptions(getItem(position).id) },
                manageMembersListener = manageMembersListener
            )
            ManageGroupMembersViewTypes.NotJoined -> NotJoinedUserViewHolder(
                parent, manageMembersListener,
            )
        }
    }

    override fun onBindViewHolder(holder: ManageMembersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}