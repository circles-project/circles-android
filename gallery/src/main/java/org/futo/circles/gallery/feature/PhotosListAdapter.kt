package org.futo.circles.gallery.feature

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.feature.picker.gallery.rooms.list.GalleryViewHolder
import org.futo.circles.core.feature.picker.gallery.rooms.list.InvitedGalleryViewHolder
import org.futo.circles.core.feature.picker.gallery.rooms.list.JoinedGalleryViewHolder
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.InvitedGalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem

private enum class GalleryListItemViewType { JoinedGallery, InvitedGallery }

class PhotosListAdapter(
    private val onRoomClicked: (GalleryListItem) -> Unit,
    private val onInviteClicked: (GalleryListItem, Boolean) -> Unit
) : BaseRvAdapter<GalleryListItem, GalleryViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedGalleryListItem -> GalleryListItemViewType.JoinedGallery.ordinal
        is InvitedGalleryListItem -> GalleryListItemViewType.InvitedGallery.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (GalleryListItemViewType.values()[viewType]) {
        GalleryListItemViewType.JoinedGallery -> JoinedGalleryViewHolder(
            parent = parent,
            onGalleryClicked = { position -> onRoomClicked(getItem(position)) }
        )

        GalleryListItemViewType.InvitedGallery -> InvitedGalleryViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            }
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}