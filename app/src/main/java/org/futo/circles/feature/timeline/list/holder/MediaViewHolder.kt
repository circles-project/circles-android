package org.futo.circles.feature.timeline.list.holder

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import org.futo.circles.core.extensions.loadEncryptedThumbOrFullIntoWithAspect
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.MediaContent

interface MediaViewHolder {

    fun unTrackMediaLoading()

    fun bindMediaCaption(content: MediaContent, textView: TextView) {
        textView.apply {
            val caption = content.captionSpanned
            setIsVisible(caption != null)
            caption?.let { setText(it, TextView.BufferType.SPANNABLE) }
        }
    }

    fun bindMediaCover(content: MediaContent, image: ImageView) {
        image.post {
            val size = content.calculateThumbnailSize(image.width)
            image.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        content.loadEncryptedThumbOrFullIntoWithAspect(image)
    }

}