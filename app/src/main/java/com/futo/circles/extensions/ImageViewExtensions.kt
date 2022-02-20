package com.futo.circles.extensions

import android.widget.ImageView
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