package com.futo.circles.feature.groups.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.GroupListItem
import com.futo.circles.model.GroupListItemPayload

class GroupsListAdapter(
    private val onGroupClicked: (GroupListItem) -> Unit
) : BaseRvAdapter<GroupListItem, GroupViewHolder>(PayloadIdEntityCallback { old, new ->
    GroupListItemPayload(
        topic = new.topic.takeIf { it != old.topic },
        isEncrypted = new.isEncrypted.takeIf { it != old.isEncrypted },
        membersCount = new.membersCount.takeIf { it != old.membersCount },
        timestamp = new.timestamp.takeIf { it != old.timestamp },
        needUpdateFullItem = new.title != old.title || new.avatarUrl != old.avatarUrl
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
                (it as? GroupListItemPayload)?.let { payload ->
                    if (payload.needUpdateFullItem)
                        holder.bind(getItem(position))
                    else
                        holder.bindPayload(payload)
                }
            }
        }
    }

}