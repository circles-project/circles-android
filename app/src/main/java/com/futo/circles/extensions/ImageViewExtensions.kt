package com.futo.circles.extensions

import android.widget.ImageView
import com.futo.circles.R
import com.squareup.picasso.Picasso
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

private const val THUMBNAIL_SIZE = 250

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
    Picasso.get().load(resolvedUrl).into(this)
}

fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}