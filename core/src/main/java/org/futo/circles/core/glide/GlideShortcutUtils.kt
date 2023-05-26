package org.futo.circles.core.glide

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.AnyThread
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import org.futo.circles.core.extensions.resolveUrl
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.util.MatrixItem

object GlideShortcutUtils {

    @AnyThread
    @Throws
    fun shortcutDrawable(
        glideRequests: GlideRequests,
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
        glideRequests: GlideRequests,
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

    private fun GlideRequest<Bitmap>.avatarOrText(
        matrixItem: MatrixItem,
        iconSize: Int
    ): GlideRequest<Bitmap> {
        return this.let {
            val resolvedUrl = MatrixSessionProvider.currentSession?.resolveUrl(matrixItem.avatarUrl)
            if (resolvedUrl != null) {
                it.load(resolvedUrl)
            } else {
                it.load(
                    TextDrawable.Builder()
                        .setShape(TextDrawable.SHAPE_RECT)
                        .setColor(ColorGenerator.DEFAULT.getColor(matrixItem))
                        .setTextColor(Color.WHITE)
                        .setWidth(iconSize)
                        .setHeight(iconSize)
                        .setText(matrixItem.firstLetterOfDisplayName())
                        .build().bitmap
                )
            }
        }
    }
}