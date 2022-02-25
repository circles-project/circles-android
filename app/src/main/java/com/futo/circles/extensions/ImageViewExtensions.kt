package com.futo.circles.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.futo.circles.R
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

const val THUMBNAIL_SIZE = 250

fun ImageView.loadMatrixThumbnail(
    avatarUrl: String?,
    resolver: ContentUrlResolver?,
    size: Int = THUMBNAIL_SIZE
) {
    val resolvedUrl = resolver?.resolveThumbnail(
        avatarUrl,
        size, size,
        ContentUrlResolver.ThumbnailMethod.SCALE
    )
    Glide.with(this).load(resolvedUrl).into(this)
}

fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}