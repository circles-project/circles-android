package org.futo.circles.feature.photos.backup

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
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

    private fun getMediaCursor(isVideo: Boolean): Cursor? {
        val projection =
            arrayOf(
                getBucketIdConstant(isVideo),
                getBucketNameConstant(isVideo),
                getMediaDataConstant(isVideo),
            )
        val sortOrder =
            if (isVideo) "${MediaStore.Video.Media.DATE_MODIFIED} DESC" else "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val uri =
            if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        return context.applicationContext.contentResolver
            .query(uri, projection, null, null, sortOrder)
    }

    private fun collectFolderData(
        folders: MutableMap<String, MediaFolderListItem>,
        cursor: Cursor?,
        isVideo: Boolean
    ) {
        cursor?.use {
            val bucketIdColumn =
                cursor.getColumnIndexOrThrow(getBucketIdConstant(isVideo))
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(getBucketNameConstant(isVideo))
            val mediaDataColumn = cursor.getColumnIndexOrThrow(
                getMediaDataConstant(isVideo)
            )
            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val file = File(cursor.getString(mediaDataColumn))
                val folderPath = file.parent ?: continue
                val fileSize = file.length()
                val size = if (folders.containsKey(bucketId))
                    (folders[bucketId]?.size ?: 0) + fileSize
                else fileSize
                folders[bucketId] = MediaFolderListItem(bucketId, bucketName, folderPath, size)
            }
        }
    }

    private fun getBucketIdConstant(isVideo: Boolean) =
        if (isVideo) MediaStore.Video.Media.BUCKET_ID else MediaStore.Images.Media.BUCKET_ID

    private fun getBucketNameConstant(isVideo: Boolean) =
        if (isVideo) MediaStore.Video.Media.BUCKET_DISPLAY_NAME else MediaStore.Images.Media.BUCKET_DISPLAY_NAME

    private fun getMediaDataConstant(isVideo: Boolean) =
        if (isVideo) MediaStore.Video.Media.DATA else MediaStore.Images.Media.DATA

    fun getMediaFolders(selectedFoldersIds: List<String>): List<MediaFolderListItem> {
        val folders = mutableMapOf<String, MediaFolderListItem>()
        collectFolderData(folders, getMediaCursor(false), false)
        collectFolderData(folders, getMediaCursor(true), true)
        return folders.values.toList().map {
            it.copy(isSelected = selectedFoldersIds.contains(it.id))
        }
    }

    fun getAllMediasToBackup(): MutableMap<String, List<MediaToBackupItem>> {
        val foldersToBackup = getInitialBackupSettings().folders
        val mediaToBackup = mutableMapOf<String, List<MediaToBackupItem>>()
        getMediaCursor(false)?.use { cursor ->
            val mediaDataColumnId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val file = File(cursor.getString(mediaDataColumnId))
                val folderPath = file.parent ?: continue
                if (!foldersToBackup.contains(folderPath)) continue
                mediaToBackup[folderPath] = mediaToBackup.getOrDefault(folderPath, emptyList())
                    .toMutableList().apply { add(file.toMediaToBackupItem()) }
            }
        }
        return mediaToBackup
    }

    fun needToBackup(path: String): Boolean {
        val parentPath = File(path).parent ?: false
        return getInitialBackupSettings().folders.contains(parentPath)
    }
}