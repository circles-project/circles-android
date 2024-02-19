package org.futo.circles.core.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.util.Log
import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVG
import jdenticon.Jdenticon
import jp.wasabeef.glide.transformations.BlurTransformation
import org.futo.circles.core.R
import org.futo.circles.core.feature.blurhash.ThumbHash
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
import org.futo.circles.core.glide.GlideApp
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.Session

fun ImageView.loadImage(url: String?) {
    Glide.with(this)
        .load(url)
        .fitCenter()
        .into(this)
}

fun ImageView.loadEncryptedImage(
    content: MediaFileData,
    preferredSize: Size? = null,
    loadOriginalSize: Boolean = false,
    thumbHash: String? = null
) {
    val loadWidth = if (loadOriginalSize) Target.SIZE_ORIGINAL else preferredSize?.width ?: width
    val loadHeight = if (loadOriginalSize) Target.SIZE_ORIGINAL else preferredSize?.height ?: height
    val placeholder = thumbHash?.let {
        BitmapDrawable(
            resources,
            ThumbHash.getBitmapFromHash(
                it,
                loadWidth.takeIf { it > 0 },
                loadHeight.takeIf { it > 0 })
        )
    }
    content.elementToDecrypt?.let {
        Log.d(
            "MyLog",
            "encrypted $loadWidth / $loadHeight"
        )
        GlideApp
            .with(context)
            .load(content)
            .placeholder(placeholder)
            .fitCenter()
            .into(this)
    } ?: loadMatrixImage(content.fileUrl, loadOriginalSize, preferredSize = preferredSize)
}

fun ImageView.loadRoomProfileIcon(
    url: String?,
    userId: String,
    loadOriginalSize: Boolean = false,
    preferredSize: Size? = null,
    session: Session? = null,
    applyBlur: Boolean = false
) {
    val backgroundColor = ColorGenerator().getColor(userId)
    var text = userId.firstOrNull()?.toString()?.uppercase() ?: ""
    if (text == "@") text = userId.elementAtOrNull(1)?.toString()?.uppercase() ?: "?"
    val placeholder = TextDrawable.Builder()
        .setShape(TextDrawable.SHAPE_ROUND_RECT)
        .setColor(backgroundColor)
        .setTextColor(Color.WHITE)
        .setText(text)
        .build()

    loadMatrixImage(url, loadOriginalSize, placeholder, preferredSize, session, applyBlur)
}

fun ImageView.loadUserProfileIcon(
    url: String?,
    userId: String,
    session: Session? = null,
    applyBlur: Boolean = false
) {
    post {
        val svgString = Jdenticon.toSvg(userId, measuredWidth)
        val svg = SVG.getFromString(svgString)
        val placeholder = PictureDrawable(svg.renderToPicture())
        loadMatrixImage(url, placeholder = placeholder, session = session, applyBlur = applyBlur)
    }
}


@SuppressLint("CheckResult")
fun ImageView.loadMatrixImage(
    url: String?,
    loadOriginalSize: Boolean = false,
    placeholder: Drawable? = null,
    preferredSize: Size? = null,
    session: Session? = null,
    applyBlur: Boolean = false
) {
    post {
        val currentSession = session ?: MatrixSessionProvider.currentSession
        val size = if (loadOriginalSize) null
        else preferredSize ?: Size(
            measuredWidth,
            measuredHeight
        ).takeIf { measuredWidth > 0 && measuredHeight > 0 }
        val resolvedUrl = currentSession?.resolveUrl(url, size)
        Glide.with(this)
            .load(resolvedUrl)
            .fitCenter()
            .placeholder(placeholder)
            .error(placeholder)
            .apply { if (applyBlur) transform(BlurTransformation(30)) }
            .into(this)
    }
}

fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}