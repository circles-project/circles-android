package org.futo.circles.feature.room.select.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.model.SelectableRoomListItem

class SelectRoomsAdapter(
    private val onRoomSelected: (SelectableRoomListItem) -> Unit
) : BaseRvAdapter<SelectableRoomListItem, SelectRoomsViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectRoomsViewHolder = SelectRoomsViewHolder(
        parent,
        onCircleClicked = { position -> onRoomSelected(getItem(position)) })


    override fun onBindViewHolder(holder: SelectRoomsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}