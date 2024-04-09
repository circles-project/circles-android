package org.futo.circles.core.feature.picker.gallery.media.list.holder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.databinding.ListItemGalleryMediaBinding
import org.futo.circles.core.extensions.onClick


class GalleryMediaItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int, View) -> Unit
) : GridMediaItemViewHolder(inflate(parent, ListItemGalleryMediaBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemGalleryMediaBinding

    override val ivCover: ImageView
        get() = binding.ivCover
    override val videoGroup: Group
        get() = binding.videoGroup
    override val tvDuration: TextView
        get() = binding.tvDuration

    init {
        onClick(itemView) { position -> onItemClicked(position, binding.ivCover) }
    }
}