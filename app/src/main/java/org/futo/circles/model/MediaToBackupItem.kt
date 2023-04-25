package org.futo.circles.model

import android.content.Context
import android.net.Uri
import org.futo.circles.core.list.IdEntity
import org.futo.circles.extensions.getUri
import java.io.File

data class MediaToBackupItem(
    override val id: String,
    val displayName: String,
    val uri: Uri,
    val size: Long
) : IdEntity<String>

fun File.toMediaToBackupItem(context: Context) =
    MediaToBackupItem(getMediaFileUniqueId(this), name, getUri(context), length())

private fun getMediaFileUniqueId(file: File): String {
    val fileName = file.name
    val fileSize = file.length()
    val fileLastModified = file.lastModified()
    return "$fileName$fileSize$fileLastModified".hashCode().toString()
}