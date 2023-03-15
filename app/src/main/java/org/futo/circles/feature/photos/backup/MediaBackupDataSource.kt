package org.futo.circles.feature.photos.backup

import android.content.Context
import android.provider.MediaStore
import org.futo.circles.model.MediaFolderListItem
import java.io.File
import kotlin.math.log10
import kotlin.math.pow

class MediaBackupDataSource(private val context: Context) {

    fun getMediaFolders(): List<MediaFolderListItem> {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
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

        val foldersList = mutableMapOf<Long, MediaFolderListItem>()
        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val bucketNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketIdColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val mediaDataColumnId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdColumnIndex)
                val bucketName = cursor.getString(bucketNameColumnIndex)
                val filePath = cursor.getString(mediaDataColumnId)
                val file = File(filePath)
                val folderPath = file.parent ?: continue
                val fileSize = file.length()
                val size = if (foldersList.containsKey(bucketId))
                    (foldersList[bucketId]?.size ?: 0) + fileSize
                else fileSize
                foldersList[bucketId] =
                    MediaFolderListItem(bucketId, bucketName, folderPath, size)
            }
        }
        return foldersList.values.toList()
    }
}