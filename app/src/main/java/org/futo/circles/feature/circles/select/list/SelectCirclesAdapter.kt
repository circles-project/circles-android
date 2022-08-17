package org.futo.circles.feature.circles.select.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.SelectableRoomListItem

class SelectCirclesAdapter(
    private val onCircleSelected: (SelectableRoomListItem) -> Unit
) : BaseRvAdapter<SelectableRoomListItem, SelectCirclesViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectCirclesViewHolder = SelectCirclesViewHolder(
        parent,
        onCircleClicked = { position -> onCircleSelected(getItem(position)) })


    override fun onBindViewHolder(holder: SelectCirclesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}