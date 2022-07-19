package org.futo.circles.core.picker.device

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import org.futo.circles.core.VideoUtils.getVideoDuration
import org.futo.circles.core.VideoUtils.getVideoThumbnail
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
                                        getVideoDuration(context, contentUri),
                                        contentUri,
                                        getVideoThumbnail(context.contentResolver, contentUri)
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

    private fun getContentUriById(id: Long): Uri = ContentUris.withAppendedId(
        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
        id
    )

    companion object {
        const val THUMBNAIL_SIZE = 250
    }
}