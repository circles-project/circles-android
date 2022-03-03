package com.futo.circles.feature.group_invite.list.selected

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.model.CirclesUser

class SelectedUsersListAdapter(
    private val onUserDeselected: (CirclesUser) -> Unit
) : BaseRvAdapter<CirclesUser, SelectedUserViewHolder>(
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