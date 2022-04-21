package com.futo.circles.extensions

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.futo.circles.core.FILE_PROVIDER_AUTHORITY_PREFIX
import com.futo.circles.core.ImageUtils
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import java.io.File

private const val UriFileScheme = "file"
private const val UriContentScheme = "content"

fun Uri.getContentUriForFileUri(
    context: Context
): Uri? = if (scheme == UriFileScheme) FileProvider.getUriForFile(
    context, context.packageName + FILE_PROVIDER_AUTHORITY_PREFIX, File(path ?: "")
) else null


fun Uri.getFilename(context: Context): String? {
    if (scheme == UriContentScheme) {
        context.contentResolver.query(this, null, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return try {
                        val index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)

                        cursor.getStringOrNull(index)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
    }
    return path?.substringAfterLast('/')
}

fun Uri.toImageContentAttachmentData(context: Context): ContentAttachmentData? {
    val projection = arrayOf(
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )
    val c = context.contentResolver.query(
        this, projection, null, null, null
    )
    return c?.use { cursor ->
        val nameColumn: Int
        val sizeColumn: Int
        try {
            nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        } catch (e: IllegalArgumentException) {
            return@use null
        }
        if (cursor.moveToNext()) {
            val name = cursor.getStringOrNull(nameColumn)
            val size = cursor.getLongOrNull(sizeColumn) ?: 0

            val bitmap = ImageUtils.getBitmap(context, this)
            val orientation = ImageUtils.getOrientation(context, this)
            val mimeType = context.contentResolver.getType(this)

            ContentAttachmentData(
                mimeType = mimeType,
                type = ContentAttachmentData.Type.IMAGE,
                name = name,
                size = size,
                height = bitmap?.height?.toLong() ?: 0,
                width = bitmap?.width?.toLong() ?: 0,
                exifOrientation = orientation,
                queryUri = this
            )
        } else {
            null
        }
    }
}