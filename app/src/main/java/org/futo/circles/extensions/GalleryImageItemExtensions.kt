package org.futo.circles.extensions

import android.util.Size
import android.widget.ImageView
import org.futo.circles.R
import org.futo.circles.model.GalleryImageListItem

fun GalleryImageListItem.loadInto(imageView: ImageView) {
    if (imageContent.fileUrl.startsWith(UriContentScheme)) {
        imageView.setImageResource(R.drawable.blurred_placeholder)
    } else {
        val imageWith = imageView.width
        val size = Size(imageWith, (imageWith / imageContent.aspectRatio).toInt())
        imageView.loadEncryptedImage(imageContent, size)
    }
}