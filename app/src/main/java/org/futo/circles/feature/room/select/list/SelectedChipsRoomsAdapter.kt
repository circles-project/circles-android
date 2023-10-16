package org.futo.circles.feature.room.select.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.feature.select_users.list.ChipItemViewHolder

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