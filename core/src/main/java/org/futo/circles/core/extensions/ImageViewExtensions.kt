package org.futo.circles.core.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Size
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.core.R
import org.futo.circles.core.feature.blurhash.ThumbHash
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
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
        Glide
            .with(context)
            .load(content)
            .placeholder(placeholder)
            .fitCenter()
            .into(this)
    } ?: loadMatrixImage(content.fileUrl)
}

fun ImageView.loadRoomProfileIcon(
    url: String?,
    userId: String
) {
    MainScope().launch {
        val session = MatrixSessionProvider.currentSession
        val placeholder =
            session?.resolveUrl(url)?.let { null } ?: getTextDrawablePlaceholder(userId)
        loadMatrixImage(url, placeholder, session)
    }
}

private suspend fun getTextDrawablePlaceholder(userId: String) = withContext(Dispatchers.IO) {
    val backgroundColor = ColorGenerator().getColor(userId)
    var text = userId.firstOrNull()?.toString()?.uppercase() ?: ""
    if (text == "@") text = userId.elementAtOrNull(1)?.toString()?.uppercase() ?: "?"
    TextDrawable.Builder()
        .setShape(TextDrawable.SHAPE_ROUND_RECT)
        .setColor(backgroundColor)
        .setTextColor(Color.WHITE)
        .setText(text)
        .build()
}

//Specify session only in case there is no active one at this moment (e.g Switch user)
fun ImageView.loadUserProfileIcon(
    url: String?,
    userId: String,
    session: Session? = MatrixSessionProvider.currentSession
) {
    MainScope().launch {
        val placeholder =
            session?.resolveUrl(url)?.let { null } ?: getTextDrawablePlaceholder(userId)
        loadMatrixImage(url, placeholder, session)
    }
}

@SuppressLint("CheckResult")
fun ImageView.loadMatrixImage(
    url: String?,
    placeholder: Drawable? = null,
    session: Session? = null
) {
    val currentSession = session ?: MatrixSessionProvider.currentSession
    val resolvedUrl = currentSession?.resolveUrl(url)
    Glide.with(this)
        .load(resolvedUrl)
        .fitCenter()
        .placeholder(placeholder)
        .error(placeholder)
        .into(this)
}

fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)
}