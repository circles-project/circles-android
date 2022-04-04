package com.futo.circles.feature.select_users.list.selected

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.UserListItem

class SelectedUsersListAdapter(
    private val onUserDeselected: (UserListItem) -> Unit
) : BaseRvAdapter<UserListItem, SelectedUserViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUserViewHolder {
        return SelectedUserViewHolder(
            parent,
            onUserDeselected = { position -> onUserDeselected(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: SelectedUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}