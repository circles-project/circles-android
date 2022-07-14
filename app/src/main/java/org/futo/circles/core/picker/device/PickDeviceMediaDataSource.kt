package org.futo.circles.core.picker.device

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.MutableLiveData
import org.futo.circles.extensions.onBG
import org.futo.circles.model.DeviceVideo
import org.futo.circles.model.DeviceVideoListItem
import java.util.concurrent.TimeUnit

class PickDeviceMediaDataSource(
    private val context: Context
) {

    val mediaLiveData = MutableLiveData<List<DeviceVideoListItem>>()

    suspend fun fetchMedia() {
        val videos = loadDeviceVideos()
        mediaLiveData.postValue(videos)
        val videosWithDurations = fetchVideosDuration(videos)
        mediaLiveData.postValue(videosWithDurations)
    }

    private suspend fun loadDeviceVideos() = onBG {
        val list = mutableListOf<DeviceVideoListItem>()
        try {
            val videoQueryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val videoProjection = arrayOf(MediaStore.Video.Media._ID)
            val videoSortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"

            val cursor = context.contentResolver.query(
                videoQueryUri,
                videoProjection,
                null,
                null,
                videoSortOrder
            )

            if (cursor != null && cursor.count > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                        val contentUri = getContentUriById(id)
                        val video = DeviceVideo(id, contentUri, 0L)
                        video.toDeviceVideoListItem()?.let { list.add(it) }
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
        } catch (ignore: Exception) {
        }
        list
    }

    private suspend fun fetchVideosDuration(
        videos: List<DeviceVideoListItem>
    ) = onBG {
        val list = mutableListOf<DeviceVideoListItem>()
        videos.forEach {
            val duration = getMediaDuration(it.contentUri)
            list.add(
                it.copy(
                    duration = duration,
                    durationString = getVideoDurationString(duration)
                )
            )
        }
        list
    }

    private fun getVideoThumbnail(contentResolver: ContentResolver, id: Long): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver.loadThumbnail(
                getContentUriById(id), Size(
                    THUMBNAIL_SIZE,
                    THUMBNAIL_SIZE
                ), null
            )
        } else {
            MediaStore.Video.Thumbnails.getThumbnail(
                contentResolver,
                id,
                MediaStore.Video.Thumbnails.MINI_KIND,
                null
            )
        }

    private fun getContentUriById(id: Long): Uri = ContentUris.withAppendedId(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        id
    )

    private fun getMediaDuration(uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        return duration?.toLongOrNull() ?: 0
    }

    private fun getVideoDurationString(duration: Long, includeHoursZeros: Boolean = false): String {
        val hours: Long = TimeUnit.MILLISECONDS.toHours(duration)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(duration) % 60

        return if (hours == 0L && !includeHoursZeros) String.format("%02d:%02d", minutes, seconds)
        else String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun DeviceVideo.toDeviceVideoListItem(): DeviceVideoListItem? = try {
        DeviceVideoListItem(
            id,
            duration,
            if (duration == 0L) "" else getVideoDurationString(duration),
            getVideoThumbnail(context.contentResolver, id),
            contentUri
        )
    } catch (exception: Exception) {
        null
    }

    companion object{
        private const val THUMBNAIL_SIZE = 500
    }
}