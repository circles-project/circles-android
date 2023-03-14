package org.futo.circles.feature.photos.backup

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import org.futo.circles.model.MediaFolder

class MediaBackupDataSource(private val context: Context) {

    fun getMediaFolders(): List<MediaFolder> {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val foldersList = HashSet<MediaFolder>()
        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val bucketNameColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val bucketIdColumnIndex =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getLong(bucketIdColumnIndex)
                val bucketName = cursor.getString(bucketNameColumnIndex)
                val mediaFolder = MediaFolder(bucketId, bucketName)
                foldersList.add(mediaFolder)
            }
        }
        return foldersList.toList()
    }
}