package org.futo.circles.feature.photos.backup

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.getRoomIdByTag
import org.futo.circles.feature.room.RoomAccountDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.model.Gallery
import org.futo.circles.model.MediaFolderListItem
import org.futo.circles.model.MediaToBackupItem
import org.futo.circles.model.toMediaToBackupItem
import java.io.File


class MediaBackupDataSource(
    private val context: Context,
    private val sendMessageDataSource: SendMessageDataSource,
    private val createRoomDataSource: CreateRoomDataSource,
    private val roomAccountDataSource: RoomAccountDataSource
) {

    suspend fun startMediaBackup() {
        val foldersToBackup = roomAccountDataSource.getMediaBackupSettings().folders
        Log.d("MyLog", "folders to backup $foldersToBackup")
        foldersToBackup.forEach { backupMediasInFolder(it) }
    }

    suspend fun startBackupByFilePath(path: String) {
        val bucketId = getBucketIdByChildFilePath(path) ?: return
        val foldersToBackup = roomAccountDataSource.getMediaBackupSettings().folders
        if (foldersToBackup.contains(bucketId)) backupMediasInFolder(bucketId)
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

    private suspend fun backupMediasInFolder(bucketId: String) {
        val roomId = createGalleryIfNotExist(bucketId)
        val dateModified = roomAccountDataSource.getMediaBackupDateModified(roomId)
        Log.d("MyLog", "saved date in $bucketId - $dateModified")
        val mediaInFolder = getMediasToBackupInBucket(bucketId, dateModified)
        Log.d(
            "MyLog",
            "media in $bucketId - ${mediaInFolder.map { "${it.id}/${it.dateModified}" }}"
        )
        mediaInFolder.forEach { item ->
            val sendCancelable = sendMessageDataSource.sendMedia(
                roomId, item.uri, null, null, MediaType.Image
            )
            Log.d("MyLog", "sent ${item.id}")
            val isUploaded = sendMessageDataSource.awaitForUploading(sendCancelable)
            if (isUploaded) {
                roomAccountDataSource.saveMediaBackupDateModified(roomId, item.dateModified)
                Log.d("MyLog", "saved ${item.id}/${item.dateModified}")
            }
        }
    }

    private suspend fun createGalleryIfNotExist(bucketId: String): String {
        var roomId = getRoomIdByTag(bucketId)
        if (roomId == null) {
            roomId = createRoomDataSource.createRoom(
                circlesRoom = Gallery(tag = bucketId),
                name = getFolderNameBy(bucketId)
            )
        }
        return roomId
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

    private fun getFolderNameBy(bucketId: String): String? {
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

    private fun getBucketIdByChildFilePath(path: String): String? {
        val projection = arrayOf(MediaStore.Images.Media.BUCKET_ID)
        val selection = "${MediaStore.Images.Media.DATA} = ?"
        val selectionArgs = arrayOf(path)
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst())
                    return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))
            }
        return null
    }

    private fun getMediasToBackupInBucket(
        bucketId: String,
        dateModified: Long?
    ): List<MediaToBackupItem> {
        var selection = "${MediaStore.Images.Media.BUCKET_ID} = $bucketId"
        dateModified?.let {
            selection += " AND ${MediaStore.Images.Media.DATE_MODIFIED} > $it"
        }
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