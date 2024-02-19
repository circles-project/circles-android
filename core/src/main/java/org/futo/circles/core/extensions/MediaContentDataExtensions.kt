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
            imageView.loadEncryptedImage(this, Size(width, height), thumbHash = thumbHash)
        }
    }
}