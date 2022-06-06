package com.futo.circles.extensions

import android.util.Size
import android.widget.ImageView
import com.futo.circles.R
import com.futo.circles.model.GalleryImageListItem

fun GalleryImageListItem.loadInto(imageView: ImageView) {
    if (imageContent.fileUrl.startsWith(UriContentScheme)) {
        imageView.setImageResource(R.drawable.blurred_placeholder)
    } else {
        val imageWith = imageView.width
        val size = Size(imageWith, (imageWith / imageContent.aspectRatio).toInt())
        imageView.loadEncryptedImage(imageContent, size)
    }
}