package org.futo.circles.model

import android.content.Context
import android.net.Uri
import org.futo.circles.core.list.IdEntity
import org.futo.circles.extensions.getUri
import java.io.File

data class MediaToBackupItem(
    val displayName: String,
    val uri: Uri,
    val size: Long,
    val dateModified: Long
) : IdEntity<Uri> {
    override val id: Uri = uri
}

fun File.toMediaToBackupItem(context: Context) = MediaToBackupItem(
    name, getUri(context), length(), lastModified()
)