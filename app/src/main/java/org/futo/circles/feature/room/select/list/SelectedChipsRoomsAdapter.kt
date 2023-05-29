package org.futo.circles.feature.room.select.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.base.ChipItemViewHolder
import org.futo.circles.model.SelectableRoomListItem

class SelectedChipsRoomsAdapter(
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