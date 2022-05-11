package com.futo.circles.feature.groups.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.GroupListItem
import com.futo.circles.model.InvitedGroupListItem
import com.futo.circles.model.JoinedGroupListItem

private enum class GroupListItemViewType { Invited, Joined }

class GroupsListAdapter(
    private val onGroupClicked: (GroupListItem) -> Unit,
    private val onInviteClicked: (GroupListItem, Boolean) -> Unit
) : BaseRvAdapter<GroupListItem, GroupViewHolder>(DefaultIdEntityCallback()) {


    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedGroupListItem -> GroupListItemViewType.Joined.ordinal
        is InvitedGroupListItem -> GroupListItemViewType.Invited.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder = when (GroupListItemViewType.values()[viewType]) {

        GroupListItemViewType.Joined -> JoinedGroupViewHolder(
            parent = parent,
            onGroupClicked = { position -> onGroupClicked(getItem(position)) }
        )
        GroupListItemViewType.Invited -> InvitedGroupViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            }
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}