package org.futo.circles.core

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import java.io.File

object ImageUtils {

    fun getImageResolution(uri: Uri): Size {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(uri.path?.let { File(it).absolutePath }, options)
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