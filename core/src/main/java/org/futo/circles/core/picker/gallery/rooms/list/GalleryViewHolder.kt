package org.futo.circles.core.picker.gallery.rooms.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.databinding.ListItemGalleryBinding
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.model.GalleryListItem

class GalleryViewHolder(
    parent: ViewGroup,
    onGalleryClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemGalleryBinding

    init {
        onClick(itemView) { position -> onGalleryClicked(position) }
    }

    fun bind(data: GalleryListItem) {
        with(binding) {
            ivGalleryImage.loadProfileIcon(data.info.avatarUrl, "")
            tvGalleryName.text = data.info.title
        }
    }
}