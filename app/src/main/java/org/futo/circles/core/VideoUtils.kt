package org.futo.circles.core

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.util.Size
import androidx.core.content.ContextCompat.getSystemService
import io.realm.Realm.getApplicationContext
import org.futo.circles.core.picker.device.PickDeviceMediaDataSource
import java.util.concurrent.TimeUnit


object VideoUtils {

    fun getVideoThumbnail(
        contentResolver: ContentResolver,
        uri: Uri,
        size: Int = PickDeviceMediaDataSource.THUMBNAIL_SIZE
    ): Bitmap = contentResolver.loadThumbnail(uri, Size(size, size), null)

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
        Size(width?.toInt() ?: 0, height?.toInt() ?: 0)
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

    fun downloadFile(url: String, title: String, description: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription(description)
        request.setTitle(title)
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_MOVIES,
            "$title.mp4"
        )
        (getApplicationContext()?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?)?.
        enqueue(request)
    }
}