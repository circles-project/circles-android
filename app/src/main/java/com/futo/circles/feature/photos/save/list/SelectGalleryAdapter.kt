package com.futo.circles.feature.photos.save.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.SelectableRoomListItem

class SelectGalleryAdapter(
    private val onGalleryClicked: (SelectableRoomListItem) -> Unit,
) : BaseRvAdapter<SelectableRoomListItem, SelectGalleryViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectGalleryViewHolder = SelectGalleryViewHolder(
        parent = parent,
        onGalleryClicked = { position -> onGalleryClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: SelectGalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}