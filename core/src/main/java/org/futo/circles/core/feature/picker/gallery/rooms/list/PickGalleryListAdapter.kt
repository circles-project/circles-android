package org.futo.circles.core.feature.picker.gallery.rooms.list


import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.GalleryListItem


class PickGalleryListAdapter(
    private val onRoomClicked: (GalleryListItem) -> Unit,
) : BaseRvAdapter<GalleryListItem, JoinedGalleryViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = JoinedGalleryViewHolder(
        parent = parent,
        onGalleryClicked = { position -> onRoomClicked(getItem(position)) }
    )

    override fun onBindViewHolder(holder: JoinedGalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}