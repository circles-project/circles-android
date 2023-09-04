package org.futo.circles.core.extensions

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import org.futo.circles.core.blurhash.ThumbHash
import org.futo.circles.core.model.MediaAttachmentInfo
import org.futo.circles.core.utils.ImageUtils
import org.futo.circles.core.utils.MediaUtils.getOrientation
import org.futo.circles.core.utils.VideoUtils
import org.matrix.android.sdk.api.session.content.ContentAttachmentData

const val UriContentScheme = "content"

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

suspend fun Uri.toImageContentAttachmentData(context: Context): ContentAttachmentData? {
    val attachmentInfo = getMediaAttachmentInfo(context) ?: return null
    val resolution = ImageUtils.getImageResolution(context, this)
    val orientation = getOrientation(context, this)
    return ContentAttachmentData(
        mimeType = attachmentInfo.mimeType,
        type = ContentAttachmentData.Type.IMAGE,
        name = attachmentInfo.name,
        size = attachmentInfo.size,
        height = resolution.height.toLong(),
        width = resolution.width.toLong(),
        exifOrientation = orientation,
        queryUri = this,
        thumbHash = ThumbHash.getThumbHash(context, this)
    )
}

suspend fun Uri.toVideoContentAttachmentData(context: Context): ContentAttachmentData? {
    val attachmentInfo = getMediaAttachmentInfo(context) ?: return null
    val duration = VideoUtils.getVideoDuration(context, this)
    val resolution = VideoUtils.getVideoResolution(context, this)
    return ContentAttachmentData(
        mimeType = attachmentInfo.mimeType,
        type = ContentAttachmentData.Type.VIDEO,
        size = attachmentInfo.size,
        height = resolution.height.toLong(),
        width = resolution.width.toLong(),
        duration = duration,
        name = attachmentInfo.name,
        queryUri = this,
        thumbHash = ThumbHash.getThumbHash(context, this)
    )
}

private fun Uri.getMediaAttachmentInfo(context: Context): MediaAttachmentInfo? {
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
            val name = cursor.getStringOrNull(nameColumn) ?: ""
            val size = cursor.getLongOrNull(sizeColumn) ?: 0
            val mimeType = context.contentResolver.getType(this)
            mimeType?.let { MediaAttachmentInfo(name, size, it) }
        } else {
            null
        }
    }
}