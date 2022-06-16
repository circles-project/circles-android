package org.futo.circles.feature.photos.gallery.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.GalleryImageListItemBinding
import org.futo.circles.extensions.loadInto
import org.futo.circles.extensions.onClick
import org.futo.circles.model.GalleryImageListItem

class GalleryImageViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder((inflate(parent, GalleryImageListItemBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GalleryImageListItemBinding

    init {
        onClick(binding.ivGalleryImage) { position -> onItemClicked(position) }
    }

    fun bind(imagePost: GalleryImageListItem) {
        imagePost.loadInto(binding.ivGalleryImage)
    }
}