package org.futo.circles.extensions

import android.util.Size
import android.widget.ImageView
import org.futo.circles.R
import org.futo.circles.model.MediaContentData

fun MediaContentData.loadEncryptedIntoWithAspect(imageView: ImageView, aspectRatio: Float) {
    if (fileUrl.startsWith(UriContentScheme)) {
        imageView.setImageResource(R.drawable.blurred_placeholder)
    } else {
        val imageWith = imageView.width
        val size = Size(imageWith, (imageWith / aspectRatio).toInt())
        imageView.loadEncryptedImage(this, size)
    }
}