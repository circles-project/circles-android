package org.futo.circles.core.feature.picker.gallery.media.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ListItemGalleryMediaBinding
import org.futo.circles.core.databinding.ListItemGalleryMediaMultiselectBinding
import org.futo.circles.core.extensions.loadEncryptedThumbOrFullIntoWithAspect
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PostContentType

abstract class GridMediaItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract val ivCover: ImageView
    abstract val videoGroup: Group
    abstract val tvDuration: TextView
    open fun bind(data: GalleryContentListItem) {
        bindCover(data.id, data.mediaContent)
        bindVideoParams(data.mediaContent)
    }

    private fun bindCover(id: String, mediaContent: MediaContent) {
        ivCover.transitionName = id
        ivCover.post {
            val size = mediaContent.calculateThumbnailSize(ivCover.width)
            ivCover.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        mediaContent.loadEncryptedThumbOrFullIntoWithAspect(ivCover)
    }

    private fun bindVideoParams(
        mediaContent: MediaContent
    ) {
        videoGroup.setIsVisible(mediaContent.type == PostContentType.VIDEO_CONTENT)
        tvDuration.text = mediaContent.mediaFileData.duration
    }

}

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

class MultiSelectGalleryMediaItemViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
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
        onClick(itemView) { position -> onItemClicked(position) }
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