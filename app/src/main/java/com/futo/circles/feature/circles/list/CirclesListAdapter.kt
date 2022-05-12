package com.futo.circles.feature.circles.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.CircleListItemPayload
import com.futo.circles.model.InvitedCircleListItem
import com.futo.circles.model.JoinedCircleListItem

private enum class CircleListItemViewType { Invited, Joined }

class CirclesListAdapter(
    private val onCircleClicked: (CircleListItem) -> Unit,
    private val onInviteClicked: (CircleListItem, Boolean) -> Unit
) : BaseRvAdapter<CircleListItem, CircleViewHolder>(PayloadIdEntityCallback { old, new ->
    if (new is JoinedCircleListItem && old is JoinedCircleListItem) {
        CircleListItemPayload(
            followersCount = new.followingCount,
            followedByCount = new.followedByCount,
            needUpdateFullItem = new.info.title != old.info.title || new.info.avatarUrl != old.info.avatarUrl
        )
    } else null
}) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedCircleListItem -> CircleListItemViewType.Joined.ordinal
        is InvitedCircleListItem -> CircleListItemViewType.Invited.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CircleViewHolder = when (CircleListItemViewType.values()[viewType]) {
        CircleListItemViewType.Joined -> JoinedCircleViewHolder(
            parent = parent,
            onCircleClicked = { position -> onCircleClicked(getItem(position)) }
        )
        CircleListItemViewType.Invited -> InvitedCircleViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            }
        )
    }

    override fun onBindViewHolder(holder: CircleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: CircleViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                if (payload is CircleListItemPayload && holder is JoinedCircleViewHolder) {
                    if (payload.needUpdateFullItem)
                        holder.bind(getItem(position))
                    else
                        holder.bindPayload(payload)
                }
            }
        }
    }
}