package com.futo.circles.feature.photos.gallery.list

import android.util.Size
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.GalleryImageListItemBinding
import com.futo.circles.extensions.UriContentScheme
import com.futo.circles.extensions.loadEncryptedImage
import com.futo.circles.extensions.onClick
import com.futo.circles.model.GalleryImageListItem

class GalleryImageViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder((inflate(parent, GalleryImageListItemBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GalleryImageListItemBinding

    init {
        onClick(itemView) { position -> onItemClicked(position) }
    }

    fun bind(imagePost: GalleryImageListItem) {
        if (imagePost.imageContent.fileUrl.startsWith(UriContentScheme)) {
            binding.ivGalleryImage.setImageResource(R.drawable.blurred_placeholder)
        } else {
            val imageWith = binding.ivGalleryImage.width
            val size = Size(imageWith, (imageWith / imagePost.imageContent.aspectRatio).toInt())
            binding.ivGalleryImage.loadEncryptedImage(imagePost.imageContent, size)
        }
    }
}