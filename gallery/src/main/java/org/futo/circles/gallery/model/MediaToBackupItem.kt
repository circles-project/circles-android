package org.futo.circles.gallery.model

import android.content.Context
import android.net.Uri
import org.futo.circles.core.extensions.getUri
import org.futo.circles.core.base.list.IdEntity
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