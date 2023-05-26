package org.futo.circles.feature.room.select_users.list.selected

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.ChipItemViewHolder
import org.futo.circles.model.UserListItem

class SelectedUsersListAdapter(
    private val onUserDeselected: (UserListItem) -> Unit
) : BaseRvAdapter<UserListItem, ChipItemViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChipItemViewHolder {
        return ChipItemViewHolder(
            parent,
            onItemDeselected = { position -> onUserDeselected(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: ChipItemViewHolder, position: Int) {
        holder.bind(getItem(position).user.name)
    }

}