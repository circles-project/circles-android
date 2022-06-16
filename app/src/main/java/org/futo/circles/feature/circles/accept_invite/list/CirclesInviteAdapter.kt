package org.futo.circles.feature.circles.accept_invite.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.RoomListItem
import org.futo.circles.model.SelectableRoomListItem

class CirclesInviteAdapter(
    private val onCircleSelected: (SelectableRoomListItem) -> Unit
) : BaseRvAdapter<SelectableRoomListItem, CirclesInviteViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CirclesInviteViewHolder = CirclesInviteViewHolder(
        parent,
        onCircleClicked = { position -> onCircleSelected(getItem(position)) })


    override fun onBindViewHolder(holder: CirclesInviteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}