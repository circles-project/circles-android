package org.futo.circles.core.extensions

import android.util.Size
import android.widget.ImageView
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData

fun MediaContent.loadEncryptedThumbOrFullIntoWithAspect(imageView: ImageView) {
    val fileContent = thumbnailFileData ?: mediaFileData
    fileContent.loadEncryptedIntoWithAspect(imageView, thumbHash)
}

fun MediaFileData.loadEncryptedIntoWithAspect(
    imageView: ImageView,
    thumbHash: String? = null
) {
    imageView.post {
        if (fileUrl.startsWith(UriContentScheme)) {
            imageView.loadImage(fileUrl)
        } else {
            val size = Size(width, (width / aspectRatio).toInt())
            imageView.loadEncryptedImage(this, size, thumbHash = thumbHash)
        }
    }
}