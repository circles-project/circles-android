package com.futo.circles.feature.manage_group_members.change_role.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.AccessLevelListItem

class ChangeAccessLevelAdapter(
    private val onLevelClicked: (AccessLevelListItem) -> Unit
) : BaseRvAdapter<AccessLevelListItem, AccessLevelViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccessLevelViewHolder = AccessLevelViewHolder(
        parent = parent,
        onRoleClicked = { position -> onLevelClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: AccessLevelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}