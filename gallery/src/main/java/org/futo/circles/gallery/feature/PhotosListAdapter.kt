package org.futo.circles.gallery.feature

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.InvitedGalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.RequestGalleryListItem
import org.futo.circles.core.picker.gallery.rooms.list.GalleryViewHolder
import org.futo.circles.core.picker.gallery.rooms.list.InvitedGalleryViewHolder
import org.futo.circles.core.picker.gallery.rooms.list.JoinedGalleryViewHolder
import org.futo.circles.core.picker.gallery.rooms.list.RequestGalleryViewHolder

private enum class GalleryListItemViewType { JoinedGallery, InvitedGallery, KnockRequest }

class PhotosListAdapter(
    private val onRoomClicked: (GalleryListItem) -> Unit,
    private val onInviteClicked: (GalleryListItem, Boolean) -> Unit,
    private val onRequestClicked: (RequestGalleryListItem, Boolean) -> Unit
) : BaseRvAdapter<GalleryListItem, GalleryViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedGalleryListItem -> GalleryListItemViewType.JoinedGallery.ordinal
        is InvitedGalleryListItem -> GalleryListItemViewType.InvitedGallery.ordinal
        is RequestGalleryListItem -> GalleryListItemViewType.KnockRequest.ordinal
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

        GalleryListItemViewType.KnockRequest -> RequestGalleryViewHolder(
            parent = parent,
            onRequestClicked = { position, isAccepted ->
                (getItem(position) as? RequestGalleryListItem)?.let {
                    onRequestClicked(it, isAccepted)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}