package org.futo.circles.feature.photos.gallery.list

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemGalleryMediaBinding
import org.futo.circles.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.GalleryContentListItem
import org.futo.circles.model.PostContentType

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
            binding.ivCover, data.mediaContent.aspectRatio
        )
        binding.videoGroup.setIsVisible(data.mediaContent.type == PostContentType.VIDEO_CONTENT)
        binding.tvDuration.text = data.mediaContent.mediaContentInfo.duration
    }
}