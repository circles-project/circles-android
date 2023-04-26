package org.futo.circles.feature.photos.backup

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import org.futo.circles.core.ROOM_BACKUP_EVENT_TYPE
import org.futo.circles.core.utils.getPhotosSpaceId
import org.futo.circles.extensions.createResult
import org.futo.circles.model.MediaBackupSettingsData
import org.futo.circles.model.MediaFolderListItem
import org.futo.circles.model.MediaToBackupItem
import org.futo.circles.model.toMediaBackupSettingsData
import org.futo.circles.model.toMediaToBackupItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import java.io.File


class MediaBackupDataSource(private val context: Context) {

    fun getInitialBackupSettingsLive(): LiveData<MediaBackupSettingsData>? =
        MatrixSessionProvider.currentSession?.getRoom(getPhotosSpaceId() ?: "")
            ?.roomAccountDataService()
            ?.getLiveAccountDataEvent(ROOM_BACKUP_EVENT_TYPE)?.map {
                it.getOrNull()?.content.toMediaBackupSettingsData()
            }

    fun getInitialBackupSettings(): MediaBackupSettingsData =
        MatrixSessionProvider.currentSession?.getRoom(getPhotosSpaceId() ?: "")
            ?.roomAccountDataService()
            ?.getAccountDataEvent(ROOM_BACKUP_EVENT_TYPE)?.content
            .toMediaBackupSettingsData()


    suspend fun saveBackupSettings(data: MediaBackupSettingsData) = createResult {
        MatrixSessionProvider.currentSession?.getRoom(getPhotosSpaceId() ?: "")
            ?.roomAccountDataService()
            ?.updateAccountData(ROOM_BACKUP_EVENT_TYPE, data.toMap())
    }

    private fun getMediaCursor(
        selection: String? = null
    ): Cursor? {
        val projection =
            arrayOf(
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        return try {
            context.contentResolver
                .query(uri, projection, selection, null, sortOrder)
        } catch (_: Exception) {
            null
        }
    }

    fun getAllMediaFolders(
        selectedFoldersIds: List<String>
    ): List<MediaFolderListItem> {
        val folders = mutableMapOf<String, MediaFolderListItem>()
        getMediaCursor()?.use { cursor ->
            val bucketIdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val mediaDataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val file = File(cursor.getString(mediaDataColumn))
                val fileSize = file.length()
                val size = if (folders.containsKey(bucketId))
                    (folders[bucketId]?.size ?: 0) + fileSize
                else fileSize
                folders[bucketId] = MediaFolderListItem(bucketId, bucketName, size)
            }
        }
        return folders.values.toList().map {
            it.copy(isSelected = selectedFoldersIds.contains(it.id))
        }
    }

    fun getFolderNameBy(bucketId: String): String? {
        val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst())
                    return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

            }
        return null
    }

    fun getMediasToBackupInBucket(bucketId: String): List<MediaToBackupItem> {
        val selection = "${MediaStore.Images.Media.BUCKET_ID} = $bucketId"
        val mediaToBackup = mutableListOf<MediaToBackupItem>()
        getMediaCursor(selection)?.use { cursor ->
            val mediaDataColumnId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val file = File(cursor.getString(mediaDataColumnId))
                mediaToBackup.add(file.toMediaToBackupItem(context))
            }
        }
        return mediaToBackup
    }
}