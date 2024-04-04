package org.futo.circles.core.feature.picker.gallery.media.list.holder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.setPadding
import org.futo.circles.core.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.databinding.ListItemGalleryMediaMultiselectBinding
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.model.GalleryContentListItem

class MultiSelectGalleryMediaItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int, View) -> Unit
) : GridMediaItemViewHolder(inflate(parent, ListItemGalleryMediaMultiselectBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemGalleryMediaMultiselectBinding

    override val ivCover: ImageView
        get() = binding.lMediaItem.ivCover
    override val videoGroup: Group
        get() = binding.lMediaItem.videoGroup
    override val tvDuration: TextView
        get() = binding.lMediaItem.tvDuration

    init {
        onClick(itemView) { position -> onItemClicked(position, binding.lMediaItem.ivCover) }
    }

    override fun bind(data: GalleryContentListItem) {
        super.bind(data)
        if (data.isSelected) {
            binding.ivSelect.setImageResource(R.drawable.ic_check_circle)
            binding.lMediaItem.lMedia.setPadding(12)
        } else {
            binding.ivSelect.setImageResource(R.drawable.ic_unselected)
            binding.lMediaItem.lMedia.setPadding(0)
        }
    }
}