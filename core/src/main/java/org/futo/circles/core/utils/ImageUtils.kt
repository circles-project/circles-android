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
        return getSizeBasedOnOrientation(
            getImageOrientation(context, uri),
            options.outWidth,
            options.outHeight
        )
    }

    fun getImageOrientation(context: Context, uri: Uri): Int {
        var orientation = 0
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            try {
                ExifInterface(inputStream).let {
                    orientation = it.rotationDegrees
                }
            } catch (ignore: Exception) {
            }
        }
        return orientation
    }

}