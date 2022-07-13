package org.futo.circles.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import org.futo.circles.core.list.IdEntity

data class DeviceVideoListItem(
    override val id: Long,
    val duration: Long,
    val durationString: String,
    val thumbnail: Bitmap,
    val contentUri: Uri
) : IdEntity<Long>

data class DeviceVideo(
    val id: Long,
    val contentUri: Uri,
    val duration: Long
)