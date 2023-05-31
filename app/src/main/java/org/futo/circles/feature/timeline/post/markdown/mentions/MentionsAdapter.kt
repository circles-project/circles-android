package org.futo.circles.feature.timeline.post.markdown.mentions

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.select_users.list.search.UserViewHolder
import org.futo.circles.model.UserListItem

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