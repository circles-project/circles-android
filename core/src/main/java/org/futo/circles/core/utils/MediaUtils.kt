package org.futo.circles.core.utils

import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.exifinterface.media.ExifInterface

object MediaUtils {

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

    fun getSizeBasedOnOrientation(context: Context, uri: Uri, width: Int, height: Int): Size {
        val orientation = getOrientation(context, uri)
        return if (orientation == 90 || orientation == 270) Size(height, width)
        else Size(width, height)
    }

}