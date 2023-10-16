package org.futo.circles.core.feature.room.manage_members.change_role.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.AccessLevelListItem

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