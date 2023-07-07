package org.futo.circles.gallery.feature.gallery.full_screen.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import org.futo.circles.core.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.gallery.databinding.ListItemImageFullScreenBinding
import org.futo.circles.gallery.databinding.ListItemVideoFullScreenBinding
import org.futo.circles.gallery.model.GalleryContentListItem

abstract class FullScreenGalleryItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: GalleryContentListItem)
}

class FullScreenImageItemViewHolder(
    parent: ViewGroup
) : FullScreenGalleryItemViewHolder((inflate(parent, ListItemImageFullScreenBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemImageFullScreenBinding

    override fun bind(data: GalleryContentListItem) {
        data.mediaContent.mediaFileData.loadEncryptedIntoWithAspect(
            binding.ivImage,
            data.mediaContent.aspectRatio,
            data.mediaContent.mediaContentInfo.thumbHash
        )
    }
}

class FullScreenVideoItemViewHolder(
    parent: ViewGroup,
    private val videoPlayer: ExoPlayer
) : FullScreenGalleryItemViewHolder((inflate(parent, ListItemVideoFullScreenBinding::inflate))) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemVideoFullScreenBinding

    init {
        binding.videoView.apply {
            player = videoPlayer
            controllerShowTimeoutMs = 3000
        }
    }

    override fun bind(data: GalleryContentListItem) {
        videoPlayer.apply {
//            setMediaItem(MediaItem.fromUri(it.second))
//            prepare()
//            play()
        }
    }
}