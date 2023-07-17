package org.futo.circles.core.picker.gallery.media.list

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.databinding.ListItemGalleryMediaBinding
import org.futo.circles.core.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.PostContentType

class GalleryMediaItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int, View) -> Unit
) : RecyclerView.ViewHolder((inflate(parent, ListItemGalleryMediaBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemGalleryMediaBinding

    init {
        onClick(itemView) { position -> onItemClicked(position, binding.ivCover) }
    }

    fun bind(data: GalleryContentListItem) {
        binding.ivCover.transitionName = data.id
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