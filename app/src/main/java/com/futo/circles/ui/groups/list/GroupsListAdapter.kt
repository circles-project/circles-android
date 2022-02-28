package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.model.GroupListItem
import com.futo.circles.model.GroupListItemPayload

class GroupsListAdapter(
    private val onGroupClicked: (GroupListItem) -> Unit
) : BaseRvAdapter<GroupListItem, GroupViewHolder>(PayloadIdEntityCallback { _, new ->
    GroupListItemPayload(
        membersCount = new.membersCount,
        timestamp = new.timestamp
    )
}) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupViewHolder = GroupViewHolder(
        parent = parent,
        onGroupClicked = { position -> onGroupClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: GroupViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                (it as? GroupListItemPayload)?.let { payload -> holder.bindPayload(payload) }
            }
        }
    }

}