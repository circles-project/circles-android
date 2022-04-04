package com.futo.circles.feature.select_users.list.search

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.HeaderItem
import com.futo.circles.model.InviteMemberListItem
import com.futo.circles.model.NoResultsItem
import com.futo.circles.model.UserListItem

private enum class InviteListViewType { Header, User, NoResults }

class InviteMembersSearchListAdapter(
    private val onUserSelected: (UserListItem) -> Unit
) : BaseRvAdapter<InviteMemberListItem, InviteMemberViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is HeaderItem -> InviteListViewType.Header.ordinal
        is UserListItem -> InviteListViewType.User.ordinal
        is NoResultsItem -> InviteListViewType.NoResults.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteMemberViewHolder {
        return when (InviteListViewType.values()[viewType]) {
            InviteListViewType.Header -> HeaderViewHolder(parent)
            InviteListViewType.User -> UserViewHolder(
                parent,
                onUserClicked = { position -> onUserSelected(getItem(position) as UserListItem) })
            InviteListViewType.NoResults -> NoResultViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: InviteMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}