package org.futo.circles.core

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Size
import org.futo.circles.core.picker.device.PickDeviceMediaDataSource
import java.util.concurrent.TimeUnit

object VideoUtils {

    fun getVideoThumbnail(
        contentResolver: ContentResolver,
        uri: Uri,
        size: Int = PickDeviceMediaDataSource.THUMBNAIL_SIZE
    ): Bitmap = contentResolver.loadThumbnail(uri, Size(size, size), null)

    fun getVideoDuration(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        return duration?.toLongOrNull() ?: 0
    }

    fun getVideoDurationString(duration: Long, includeHoursZeros: Boolean = false): String {
        val hours: Long = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(duration) % 60

        return if (hours == 0L && !includeHoursZeros) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}