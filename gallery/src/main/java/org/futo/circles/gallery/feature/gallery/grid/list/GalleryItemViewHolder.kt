package org.futo.circles.gallery.feature.gallery.grid.list

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.model.PostContentType
import org.futo.circles.gallery.databinding.ListItemGalleryMediaBinding
import org.futo.circles.gallery.model.GalleryContentListItem

class GalleryItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder((inflate(parent, ListItemGalleryMediaBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemGalleryMediaBinding

    init {
        onClick(itemView) { position -> onItemClicked(position) }
    }

    fun bind(data: GalleryContentListItem) {
        binding.ivCover.post {
            val size = data.mediaContent.calculateSize(binding.ivCover.width)
            binding.ivCover.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        data.mediaContent.mediaFileData.loadEncryptedIntoWithAspect(
            binding.ivCover,
            data.mediaContent.aspectRatio,
            data.mediaContent.mediaContentInfo.thumbHash
        )
        binding.videoGroup.setIsVisible(data.mediaContent.type == PostContentType.VIDEO_CONTENT)
        binding.tvDuration.text = data.mediaContent.mediaContentInfo.duration
    }
}