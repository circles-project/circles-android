package org.futo.circles.feature.photos.backup

import android.content.Context
import android.provider.MediaStore
import org.futo.circles.core.ROOM_BACKUP_EVENT_TYPE
import org.futo.circles.core.utils.getPhotosSpaceId
import org.futo.circles.extensions.createResult
import org.futo.circles.model.MediaBackupSettingsData
import org.futo.circles.model.MediaBackupSettingsData.Companion.backupOverWifiKey
import org.futo.circles.model.MediaBackupSettingsData.Companion.foldersKey
import org.futo.circles.model.MediaBackupSettingsData.Companion.isBackupEnabledKey
import org.futo.circles.model.MediaFolderListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.getRoom
import java.io.File

class MediaBackupDataSource(private val context: Context) {

    fun getInitialBackupSettings(): MediaBackupSettingsData {
        val content = MatrixSessionProvider.currentSession?.getRoom(getPhotosSpaceId() ?: "")
            ?.roomAccountDataService()
            ?.getAccountDataEvent(ROOM_BACKUP_EVENT_TYPE)?.content
        return createMediaBackupData(content)
    }

    suspend fun saveBackupSettings(data: MediaBackupSettingsData) = createResult {
        MatrixSessionProvider.currentSession?.getRoom(getPhotosSpaceId() ?: "")
            ?.roomAccountDataService()
            ?.updateAccountData(ROOM_BACKUP_EVENT_TYPE, data.toMap())
    }

    fun getMediaFolders(selectedFoldersIds: List<String>): List<MediaFolderListItem> {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )
        val collection = MediaStore.Files.getContentUri("external")
        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        val foldersList = mutableMapOf<String, MediaFolderListItem>()
        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val bucketNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val mediaDataColumnId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val bucketName = cursor.getString(bucketNameColumnIndex)
                val file = File(cursor.getString(mediaDataColumnId))
                val folderPath = file.parent ?: continue
                val fileSize = file.length()
                val size = if (foldersList.containsKey(folderPath))
                    (foldersList[folderPath]?.size ?: 0) + fileSize
                else fileSize
                foldersList[folderPath] =
                    MediaFolderListItem(
                        bucketName, folderPath, size,
                        selectedFoldersIds.contains(folderPath)
                    )
            }
        }
        return foldersList.values.toList()
    }

    fun getAllMediasToBackup() {

    }

    @Suppress("UNCHECKED_CAST")
    private fun createMediaBackupData(content: Content?) = MediaBackupSettingsData(
        (content?.get(isBackupEnabledKey) as? Boolean) ?: false,
        (content?.get(backupOverWifiKey) as? Boolean) ?: false,
        (content?.get(foldersKey) as? List<String>) ?: emptyList()
    )

    private fun getMediaFileUniqueId(filePath: String): String {
        val file = File(filePath)
        val fileName = file.name
        val fileSize = file.length()
        val fileLastModified = file.lastModified()
        return "$fileName$fileSize$fileLastModified".hashCode().toString()
    }

    fun needToBackup(path: String): Boolean {
        val parentPath = File(path).parent
        return getInitialBackupSettings().folders.contains(parentPath)
    }
}