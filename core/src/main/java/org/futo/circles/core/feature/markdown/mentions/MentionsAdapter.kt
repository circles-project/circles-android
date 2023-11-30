package org.futo.circles.core.feature.markdown.mentions

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.UserListItem
import org.futo.circles.core.feature.select_users.list.search.UserViewHolder

class MentionsAdapter(
    private val onUserSelected: (UserListItem) -> Unit
) : BaseRvAdapter<UserListItem, UserViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            parent,
            onUserClicked = { position -> onUserSelected(getItem(position) as UserListItem) })

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}