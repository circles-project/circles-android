package com.futo.circles.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.futo.circles.R
import com.futo.circles.glide.GlideApp
import com.futo.circles.model.ImageContent
import com.futo.circles.provider.MatrixSessionProvider


fun ImageView.loadImage(
    url: String?,
    size: Int = Target.SIZE_ORIGINAL
) {
    val resolvedUrl = MatrixSessionProvider.currentSession?.resolveUrl(url, size)
    Glide.with(this)
        .load(resolvedUrl)
        .override(size, size)
        .fitCenter()
        .into(this)
}


fun ImageView.loadEncryptedImage(content: ImageContent, size: Int = Target.SIZE_ORIGINAL) {
    val request = content.elementToDecrypt?.let {
        GlideApp
            .with(context)
            .load(content)
    } ?: run {
        val resolvedUrl = MatrixSessionProvider.currentSession?.resolveUrl(content.fileUrl, size)
        GlideApp
            .with(context)
            .load(resolvedUrl)
    }
    request.override(size, size)
        .fitCenter()
        .into(this)
}


fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}