package org.futo.circles.core.picker.gallery.rooms.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.model.GalleryListItem

class PhotosListAdapter(
    private val onRoomClicked: (GalleryListItem) -> Unit
) : BaseRvAdapter<GalleryListItem, GalleryViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = GalleryViewHolder(
        parent = parent,
        onGalleryClicked = { position -> onRoomClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}