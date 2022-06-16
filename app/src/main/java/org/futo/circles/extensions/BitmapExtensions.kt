package org.futo.circles.extensions

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun Bitmap.saveImageToDeviceGallery(context: Context) {
    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val fileName = UUID.randomUUID().toString().replace("-", "")
    val imageFile = File(imagesDir, "$fileName.jpeg")
    FileOutputStream(imageFile).use { compress(Bitmap.CompressFormat.JPEG, 100, it) }
    MediaScannerConnection.scanFile(context, arrayOf(imageFile.absolutePath), null, null)
}