package com.futo.circles.extensions

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Size
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.futo.circles.R
import com.futo.circles.glide.GlideApp
import com.futo.circles.model.ImageContent
import com.futo.circles.provider.MatrixSessionProvider


fun ImageView.loadImage(
    url: String?,
    loadOriginalSize: Boolean = false,
    placeholder: Drawable? = null,
    preferredSize: Size? = null
) {
    val size = if (loadOriginalSize) null else preferredSize ?: Size(width, height)
    val resolvedUrl = MatrixSessionProvider.currentSession?.resolveUrl(url, size)
    Glide.with(this)
        .load(resolvedUrl)
        .fitCenter()
        .error(placeholder)
        .into(this)
}


fun ImageView.loadEncryptedImage(
    content: ImageContent, preferredSize: Size? = null, loadOriginalSize: Boolean = false
) {
    val loadWidth = if (loadOriginalSize) Target.SIZE_ORIGINAL else preferredSize?.width ?: width
    val loadHeight = if (loadOriginalSize) Target.SIZE_ORIGINAL else preferredSize?.height ?: height

    content.elementToDecrypt?.let {
        GlideApp
            .with(context)
            .load(content)
            .override(loadWidth, loadHeight)
            .fitCenter()
            .into(this)
    } ?: loadImage(content.fileUrl, loadOriginalSize, preferredSize = preferredSize)
}

fun ImageView.loadProfileIcon(
    url: String?,
    userId: String,
    loadOriginalSize: Boolean = false,
    preferredSize: Size? = null
) {

    val backgroundColor = ColorGenerator.DEFAULT.getColor(userId)

    val text = userId.firstOrNull()?.toString()?.uppercase() ?: "?"
    val placeholder = TextDrawable.Builder()
        .setShape(TextDrawable.SHAPE_ROUND)
        .setColor(backgroundColor)
        .setTextColor(Color.WHITE)
        .setText(text)
        .build()

    loadImage(url, loadOriginalSize, placeholder, preferredSize)
}


fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}