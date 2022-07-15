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
import androidx.lifecycle.map
import org.futo.circles.extensions.onBG
import org.futo.circles.model.DeviceImageListItem
import org.futo.circles.model.DeviceMediaListItem
import org.futo.circles.model.DeviceVideoListItem

class PickDeviceMediaDataSource(
    private val context: Context
) {

    private val mediaLiveData = MutableLiveData<List<DeviceMediaListItem>>()

    fun getMediaLiveData(isVideoAvailable: Boolean) =
        mediaLiveData.map { if (isVideoAvailable) it else it.filterIsInstance<DeviceImageListItem>() }

    suspend fun fetchMedia() {
        loadDeviceMedia()
        addVideosDuration()
    }

    private suspend fun loadDeviceMedia() {
        onBG {
            val list = mutableListOf<DeviceMediaListItem>()
            try {
                val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.MEDIA_TYPE
                )
                val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                val sortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"

                val cursor = context.contentResolver.query(
                    queryUri,
                    projection,
                    selection,
                    null,
                    sortOrder
                )

                if (cursor != null && cursor.count > 0) {
                    if (cursor.moveToFirst()) {
                        do {
                            val id =
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                            val mediaType =
                                cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE))
                            val contentUri = getContentUriById(id)

                            val item =
                                if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                                    DeviceVideoListItem(
                                        id,
                                        0L,
                                        contentUri,
                                        getVideoThumbnail(context.contentResolver, id)
                                    )
                                } else {
                                    DeviceImageListItem(
                                        id,
                                        contentUri
                                    )
                                }
                            list.add(item)
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                }
            } catch (ignore: Exception) {
            }

            mediaLiveData.postValue(list)
        }
    }

    private suspend fun addVideosDuration() {
        onBG {
            val newList = mutableListOf<DeviceMediaListItem>()
            val list = mediaLiveData.value?.toMutableList() ?: mutableListOf()
            list.forEach {
                val item = when (it) {
                    is DeviceImageListItem -> it
                    is DeviceVideoListItem -> {
                        val duration = getMediaDuration(it.contentUri)
                        it.copy(duration = duration)
                    }
                }
                newList.add(item)
            }
            mediaLiveData.postValue(newList)
        }
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
        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
        id
    )

    private fun getMediaDuration(uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()

        return duration?.toLongOrNull() ?: 0
    }

    companion object {
        const val THUMBNAIL_SIZE = 250
    }
}