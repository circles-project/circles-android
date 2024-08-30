package org.futo.circles.core.feature.room.requests.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestHeaderItem
import org.futo.circles.core.model.RoomRequestListItem

enum class RoomRequestViewType { Header, Knock, Invite }

class RoomRequestsAdapter(
    private val onInviteClicked: (RoomInviteListItem, Boolean) -> Unit,
    private val onKnockClicked: (KnockRequestListItem, Boolean) -> Unit
) : BaseRvAdapter<RoomRequestListItem, RoomRequestViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is KnockRequestListItem -> RoomRequestViewType.Knock.ordinal
        is RoomRequestHeaderItem -> RoomRequestViewType.Header.ordinal
        is RoomInviteListItem -> RoomRequestViewType.Invite.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (RoomRequestViewType.entries[viewType]) {
        RoomRequestViewType.Invite -> InviteRequestViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                (getItem(position) as? RoomInviteListItem)?.let {
                    onInviteClicked(it, isAccepted)
                }
            })

        RoomRequestViewType.Knock -> KnockRequestViewHolder(
            parent = parent,
            onRequestClicked = { position, isAccepted ->
                (getItem(position) as? KnockRequestListItem)?.let {
                    onKnockClicked(it, isAccepted)
                }
            }
        )

        RoomRequestViewType.Header -> RoomRequestHeaderViewHolder(parent = parent)
    }


    override fun onBindViewHolder(holder: RoomRequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}