package org.futo.circles.feature.photos.gallery.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.GalleryImageListItemBinding
import org.futo.circles.databinding.GalleryVideoListItemBinding
import org.futo.circles.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.extensions.onClick
import org.futo.circles.model.GalleryContentListItem
import org.futo.circles.model.GalleryImageListItem
import org.futo.circles.model.GalleryVideoListItem

abstract class GalleryContentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: GalleryContentListItem)
}

class GalleryImageViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : GalleryContentViewHolder((inflate(parent, GalleryImageListItemBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GalleryImageListItemBinding

    init {
        onClick(binding.ivGalleryImage) { position -> onItemClicked(position) }
    }

    override fun bind(data: GalleryContentListItem) {
        if (data !is GalleryImageListItem) return
        data.imageContent.mediaContentData.loadEncryptedIntoWithAspect(
            binding.ivGalleryImage, data.imageContent.aspectRatio
        )

    }
}

class GalleryVideoViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : GalleryContentViewHolder((inflate(parent, GalleryVideoListItemBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GalleryVideoListItemBinding

    init {
        onClick(itemView) { position -> onItemClicked(position) }
    }

    override fun bind(data: GalleryContentListItem) {
        if (data !is GalleryVideoListItem) return
        data.videoContent.mediaContentData.loadEncryptedIntoWithAspect(
            binding.ivVideoCover, data.videoContent.aspectRatio
        )
        binding.tvDuration.text = data.videoContent.duration
    }
}