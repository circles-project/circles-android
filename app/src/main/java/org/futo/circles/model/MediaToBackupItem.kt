package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import java.io.File

data class MediaToBackupItem(
    override val id: String,
    val displayName: String,
    val path: String,
    val size: Long
) : IdEntity<String>

fun File.toMediaToBackupItem() = MediaToBackupItem(getMediaFileUniqueId(this), name, path, length())

private fun getMediaFileUniqueId(file: File): String {
    val fileName = file.name
    val fileSize = file.length()
    val fileLastModified = file.lastModified()
    return "$fileName$fileSize$fileLastModified".hashCode().toString()
}