package org.futo.circles.feature.circles.accept_invite.list.selected

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.list.ChipItemViewHolder
import org.futo.circles.model.RoomListItem
import org.futo.circles.model.SelectableRoomListItem

class SelectedCirclesAdapter(
    private val onCircleDeselected: (SelectableRoomListItem) -> Unit
) : BaseRvAdapter<SelectableRoomListItem, ChipItemViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipItemViewHolder {
        return ChipItemViewHolder(
            parent,
            onItemDeselected = { position -> onCircleDeselected(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: ChipItemViewHolder, position: Int) {
        holder.bind(getItem(position).info.title)
    }

}