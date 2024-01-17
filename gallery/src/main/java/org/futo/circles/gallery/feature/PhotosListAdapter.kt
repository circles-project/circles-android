package org.futo.circles.gallery.feature

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.feature.picker.gallery.rooms.list.GalleryInviteNotificationViewHolder
import org.futo.circles.core.feature.picker.gallery.rooms.list.GalleryViewHolder
import org.futo.circles.core.feature.picker.gallery.rooms.list.JoinedGalleryViewHolder
import org.futo.circles.core.model.GalleryInvitesNotificationListItem
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem

private enum class GalleryListItemViewType { JoinedGallery, InviteNotification }

class PhotosListAdapter(
    private val onRoomClicked: (GalleryListItem) -> Unit,
    private val onOpenInvitesClicked: () -> Unit
) : BaseRvAdapter<GalleryListItem, GalleryViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedGalleryListItem -> GalleryListItemViewType.JoinedGallery.ordinal
        is GalleryInvitesNotificationListItem -> GalleryListItemViewType.InviteNotification.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (GalleryListItemViewType.entries[viewType]) {
        GalleryListItemViewType.JoinedGallery -> JoinedGalleryViewHolder(
            parent = parent,
            onGalleryClicked = { position -> onRoomClicked(getItem(position)) }
        )

        GalleryListItemViewType.InviteNotification -> GalleryInviteNotificationViewHolder(
            parent = parent, onClicked = { onOpenInvitesClicked() }
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}