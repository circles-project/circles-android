package org.futo.circles.core.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import androidx.exifinterface.media.ExifInterface

object ImageUtils {

    fun getImageResolution(context: Context, uri: Uri): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(uri),
            null,
            options
        )
        return Size(options.outWidth, options.outHeight)
    }

    fun getOrientation(context: Context, uri: Uri): Int {
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