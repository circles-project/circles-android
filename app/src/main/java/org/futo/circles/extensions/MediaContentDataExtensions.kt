package org.futo.circles.extensions

import android.util.Size
import android.widget.ImageView
import org.futo.circles.model.MediaFileData

fun MediaFileData.loadEncryptedIntoWithAspect(
    imageView: ImageView,
    aspectRatio: Float,
    thumbHash: String? = null
) {
    imageView.post {
        if (fileUrl.startsWith(UriContentScheme)) {
            imageView.loadImage(fileUrl)
        } else {
            val imageWith = imageView.width
            val size = Size(imageWith, (imageWith / aspectRatio).toInt())
            imageView.loadEncryptedImage(this, size, thumbHash = thumbHash)
        }
    }
}