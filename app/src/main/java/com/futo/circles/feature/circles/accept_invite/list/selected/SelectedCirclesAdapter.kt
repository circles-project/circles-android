package com.futo.circles.feature.circles.accept_invite.list.selected

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.core.list.ChipItemViewHolder
import com.futo.circles.model.RoomListItem
import com.futo.circles.model.SelectableRoomListItem

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