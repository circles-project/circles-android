package org.futo.circles.model

import android.graphics.Bitmap
import android.net.Uri
import org.futo.circles.core.utils.VideoUtils.getVideoDurationString
import org.futo.circles.core.list.IdEntity
import org.futo.circles.core.picker.MediaType

sealed class DeviceMediaListItem(
    open val contentUri: Uri,
    open val type: MediaType
) : IdEntity<Long>

data class DeviceImageListItem(
    override val id: Long,
    override val contentUri: Uri,
    override val type: MediaType = MediaType.Image
) : DeviceMediaListItem(contentUri, type)

data class DeviceVideoListItem(
    override val id: Long,
    val duration: Long,
    override val contentUri: Uri,
    val thumbnail: Bitmap,
    override val type: MediaType = MediaType.Video
) : DeviceMediaListItem(contentUri, type) {

    val durationString: String = if (duration == 0L) "" else getVideoDurationString(duration)
}