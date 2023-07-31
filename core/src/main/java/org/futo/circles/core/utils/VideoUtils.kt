package org.futo.circles.core.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Size
import org.futo.circles.core.utils.MediaUtils.getSizeBasedOnOrientation
import java.util.concurrent.TimeUnit


object VideoUtils {

    fun getVideoDuration(context: Context, uri: Uri): Long = try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        duration?.toLongOrNull() ?: 0L
    } catch (e: Exception) {
        0L
    }

    fun getVideoResolution(context: Context, uri: Uri): Size = try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        retriever.release()
        getSizeBasedOnOrientation(
            context, uri,
            width?.toInt() ?: 0,
            height?.toInt() ?: 0
        )
    } catch (e: Exception) {
        Size(0, 0)
    }

    fun getVideoDurationString(duration: Long, includeHoursZeros: Boolean = false): String {
        val hours: Long = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(duration) % 60

        return if (hours == 0L && !includeHoursZeros) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}