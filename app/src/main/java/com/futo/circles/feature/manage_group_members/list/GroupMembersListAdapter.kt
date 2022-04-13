package com.futo.circles.feature.manage_group_members.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.view.ManageMembersOptionsListener

class GroupMembersListAdapter(
    private val onToggleOptions: (String) -> Unit,
    private val manageMembersListener: ManageMembersOptionsListener
) : BaseRvAdapter<GroupMemberListItem, GroupMemberViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        return GroupMemberViewHolder(
            parent = parent,
            onUserClicked = { position -> onToggleOptions(getItem(position).id) },
            manageMembersListener = manageMembersListener
        )
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}