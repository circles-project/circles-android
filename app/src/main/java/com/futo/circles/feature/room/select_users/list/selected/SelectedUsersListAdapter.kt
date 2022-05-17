package com.futo.circles.feature.room.select_users.list.selected

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.core.list.ChipItemViewHolder
import com.futo.circles.model.UserListItem

class SelectedUsersListAdapter(
    private val onUserDeselected: (UserListItem) -> Unit
) : BaseRvAdapter<UserListItem, ChipItemViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipItemViewHolder {
        return ChipItemViewHolder(
            parent,
            onItemDeselected = { position -> onUserDeselected(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: ChipItemViewHolder, position: Int) {
        holder.bind(getItem(position).user.name)
    }

}