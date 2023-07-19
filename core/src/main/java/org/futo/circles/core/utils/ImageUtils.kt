package org.futo.circles.core.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import org.futo.circles.core.utils.MediaUtils.getSizeBasedOnOrientation

object ImageUtils {

    fun getImageResolution(context: Context, uri: Uri): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri),
            null,
            options
        )
        return getSizeBasedOnOrientation(context, uri, options.outWidth, options.outHeight)
    }

}