package com.futo.circles.feature.circles.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.CircleListItemPayload

class CirclesListAdapter(
    private val onCircleClicked: (CircleListItem) -> Unit
) : BaseRvAdapter<CircleListItem, CircleViewHolder>(PayloadIdEntityCallback { old, new ->
    CircleListItemPayload(
        followersCount = new.followingCount,
        followedByCount = new.followedByCount,
        needUpdateFullItem = new.name != old.name || new.avatarUrl != old.avatarUrl
    )
}) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CircleViewHolder = CircleViewHolder(
        parent = parent,
        onCircleClicked = { position -> onCircleClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: CircleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: CircleViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                (it as? CircleListItemPayload)?.let { payload ->
                    if (payload.needUpdateFullItem)
                        holder.bind(getItem(position))
                    else
                        holder.bindPayload(payload)
                }
            }
        }
    }

}