package com.futo.circles.feature.group_invite.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.model.RoomMemberListItem

class InviteMembersListAdapter() : BaseRvAdapter<RoomMemberListItem, InviteMemberViewHolder>(
    DefaultIdEntityCallback()
) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InviteMemberViewHolder = InviteMemberViewHolder(parent = parent)

    override fun onBindViewHolder(holder: InviteMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}