package com.futo.circles.extensions

import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.futo.circles.R
import com.futo.circles.glide.GlideApp
import com.futo.circles.model.ImageContent
import com.futo.circles.provider.MatrixSessionProvider


fun ImageView.loadImage(
    url: String?,
    size: Size? = null
) {
    val resolvedUrl = MatrixSessionProvider.currentSession?.resolveUrl(url, size)
    Glide.with(this)
        .load(resolvedUrl)
        .fitCenter()
        .into(this)
}


fun ImageView.loadEncryptedImage(
    content: ImageContent, size: Size? = null
) {
    content.elementToDecrypt?.let {
        GlideApp
            .with(context)
            .load(content)
            .override(size?.width ?: Target.SIZE_ORIGINAL, size?.height ?: Target.SIZE_ORIGINAL)
            .fitCenter()
            .into(this)
    } ?: loadImage(content.fileUrl, size)
}


fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}