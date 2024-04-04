package org.futo.circles.core.feature.picker.gallery.media.list.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.constraintlayout.widget.Group
import androidx.core.view.updateLayoutParams
import org.futo.circles.core.extensions.loadEncryptedThumbOrFullIntoWithAspect
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.model.GalleryTimelineListItem
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PostContentType

abstract class GridMediaItemViewHolder(view: View) : GalleryTimelineItemViewHolder(view) {

    abstract val ivCover: ImageView
    abstract val videoGroup: Group
    abstract val tvDuration: TextView

    override fun bind(item: GalleryTimelineListItem) {
        (item as? GalleryContentListItem)?.let { bind(it) }
    }

    @CallSuper
    protected open fun bind(data: GalleryContentListItem) {
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