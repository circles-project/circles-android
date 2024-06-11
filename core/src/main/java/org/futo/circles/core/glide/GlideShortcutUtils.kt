package org.futo.circles.core.glide

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.AnyThread
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import org.futo.circles.core.extensions.resolveUrl
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.util.MatrixItem

object GlideShortcutUtils {

    @AnyThread
    @Throws
    fun shortcutDrawable(
        glideRequests: RequestManager,
        matrixItem: MatrixItem,
        iconSize: Int
    ): Bitmap {
        return glideRequests
            .asBitmap()
            .avatarOrText(matrixItem, iconSize)
            .apply(RequestOptions.centerCropTransform())
            .submit(iconSize, iconSize)
            .get()
    }

    @AnyThread
    @Throws
    fun adaptiveShortcutDrawable(
        glideRequests: RequestManager,
        matrixItem: MatrixItem, iconSize: Int,
        adaptiveIconSize: Int,
        adaptiveIconOuterSides: Float
    ): Bitmap {
        return glideRequests
            .asBitmap()
            .avatarOrText(matrixItem, iconSize)
            .transform(
                CenterCrop(),
                AdaptiveIconTransformation(adaptiveIconSize, adaptiveIconOuterSides)
            )
            .signature(ObjectKey("adaptive-icon"))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .submit(iconSize, iconSize)
            .get()
    }

    private fun RequestBuilder<Bitmap>.avatarOrText(
        matrixItem: MatrixItem,
        iconSize: Int
    ): RequestBuilder<Bitmap> {
        return this.let {
            val resolvedUrl = MatrixSessionProvider.currentSession?.resolveUrl(matrixItem.avatarUrl)
            if (resolvedUrl != null) {
                it.load(resolvedUrl)
            } else {
                it.load(
                    TextDrawable.Builder()
                        .setShape(TextDrawable.SHAPE_RECT)
                        .setColor(ColorGenerator().getColor(matrixItem))
                        .setTextColor(Color.WHITE)
                        .setWidth(iconSize)
                        .setHeight(iconSize)
                        .setText(matrixItem.firstLetterOfDisplayName())
                        .build().getBitmap()
                )
            }
        }
    }
}