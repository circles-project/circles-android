package org.futo.circles.core.feature.select_users.list.search

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.HeaderItem
import org.futo.circles.core.model.InviteMemberListItem
import org.futo.circles.core.model.NoResultsItem
import org.futo.circles.core.model.UserListItem

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
        return when (InviteListViewType.entries[viewType]) {
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