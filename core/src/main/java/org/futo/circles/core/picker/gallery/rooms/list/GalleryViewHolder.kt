package org.futo.circles.core.picker.gallery.rooms.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ListItemInvitedGalleryBinding
import org.futo.circles.core.databinding.ListItemJoinedGalleryBinding
import org.futo.circles.core.databinding.ListItemRequestGalleryBinding
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.InvitedGalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.RequestGalleryListItem


abstract class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: GalleryListItem)
}

class JoinedGalleryViewHolder(
    parent: ViewGroup,
    onGalleryClicked: (Int) -> Unit
) : GalleryViewHolder(inflate(parent, ListItemJoinedGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemJoinedGalleryBinding

    init {
        onClick(itemView) { position -> onGalleryClicked(position) }
    }

    override fun bind(data: GalleryListItem) {
        if (data !is JoinedGalleryListItem) return

        with(binding) {
            ivGalleryImage.loadProfileIcon(data.info.avatarUrl, "")
            tvGalleryName.text = data.info.title
        }
    }
}

class InvitedGalleryViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : GalleryViewHolder(inflate(parent, ListItemInvitedGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGalleryBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: GalleryListItem) {
        if (data !is InvitedGalleryListItem) return

        with(binding) {
            tvGalleryTitle.text = data.info.title
            ivGallery.loadProfileIcon(data.info.avatarUrl, data.info.title)
            tvInviterName.text = context.getString(R.string.invited_by_format, data.inviterName)
        }
    }
}

class RequestGalleryViewHolder(
    parent: ViewGroup,
    onRequestClicked: (Int, Boolean) -> Unit
) : GalleryViewHolder(inflate(parent, ListItemRequestGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemRequestGalleryBinding

    init {
        onClick(binding.btnInvite) { position -> onRequestClicked(position, true) }
        onClick(binding.btnDecline) { position -> onRequestClicked(position, false) }
    }

    override fun bind(data: GalleryListItem) {
        if (data !is RequestGalleryListItem) return

        with(binding) {
            binding.tvRequestUserId.text = context.getString(
                R.string.requested_to_join_format, data.requesterName
            )
            binding.tvRoomName.text = data.info.title
            ivGallery.loadProfileIcon(data.info.avatarUrl, data.info.title)
        }
    }
}