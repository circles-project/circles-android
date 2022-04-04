package com.futo.circles.feature.manage_group_members.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.GroupMemberListItem

class GroupMembersListAdapter : BaseRvAdapter<GroupMemberListItem, GroupMemberViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberViewHolder {
        return GroupMemberViewHolder(parent)
    }

    override fun onBindViewHolder(holder: GroupMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}