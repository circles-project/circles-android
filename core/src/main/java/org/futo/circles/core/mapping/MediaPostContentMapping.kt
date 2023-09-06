package org.futo.circles.core.mapping

import com.bumptech.glide.request.target.Target
import org.futo.circles.core.MediaCaptionFieldKey
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.utils.VideoUtils
import org.matrix.android.sdk.api.session.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.matrix.android.sdk.api.session.room.model.message.getFileName
import org.matrix.android.sdk.api.session.room.model.message.getFileUrl
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun TimelineEvent.toMediaContent(mediaType: MediaType): MediaContent = MediaContent(
    type = if (mediaType == MediaType.Image) PostContentType.IMAGE_CONTENT else PostContentType.VIDEO_CONTENT,
    caption = getCaption(),
    mediaFileData = toMediaFileData(mediaType),
    thumbnailFileData = toThumbnailFileData(mediaType),
    thumbHash = getThumbHash(mediaType)
)

private fun TimelineEvent.getCaption(): String? {
    val lastContent =
        annotations?.editSummary?.latestEdit?.getClearContent() ?: root.getClearContent()
    return lastContent?.get(MediaCaptionFieldKey)?.toString()
}

private fun TimelineEvent.getThumbHash(mediaType: MediaType) = when (mediaType) {
    MediaType.Image -> {
        val info = root.getClearContent()?.toModel<MessageImageContent>()?.info
        info?.thumbHash ?: info?.blurHash
    }

    MediaType.Video -> {
        val info = root.getClearContent()?.toModel<MessageVideoContent>()?.videoInfo
        info?.thumbHash ?: info?.blurHash
    }
}

private fun TimelineEvent.toMediaFileData(mediaType: MediaType): MediaFileData {
    val content = root.getClearContent()
    return when (mediaType) {
        MediaType.Image -> content.toModel<MessageImageContent>().toMediaFileData()
        MediaType.Video -> content.toModel<MessageVideoContent>().toMediaFileData()
    }
}

private fun MessageImageContent?.toMediaFileData() = MediaFileData(
    fileName = this?.getFileName() ?: "",
    mimeType = this?.mimeType ?: "",
    fileUrl = this?.getFileUrl() ?: "",
    elementToDecrypt = this?.encryptedFileInfo?.toElementToDecrypt(),
    width = this?.info?.width ?: Target.SIZE_ORIGINAL,
    height = this?.info?.height ?: Target.SIZE_ORIGINAL,
    duration = ""
)

private fun MessageVideoContent?.toMediaFileData() = MediaFileData(
    fileName = this?.getFileName() ?: "",
    mimeType = this?.mimeType ?: "",
    fileUrl = this?.getFileUrl() ?: "",
    elementToDecrypt = this?.encryptedFileInfo?.toElementToDecrypt(),
    width = this?.videoInfo?.width ?: Target.SIZE_ORIGINAL,
    height = this?.videoInfo?.height ?: Target.SIZE_ORIGINAL,
    duration = VideoUtils.getVideoDurationString(this?.videoInfo?.duration?.toLong() ?: 0L)
)

private fun MessageImageContent.toThumbnailFileData(): MediaFileData? {
    val imageInfo = info ?: return null
    val file = imageInfo.thumbnailFile?.toElementToDecrypt() ?: return null
    val url = imageInfo.thumbnailFile?.url ?: imageInfo.thumbnailUrl
    val mimeType = imageInfo.thumbnailInfo?.mimeType ?: ""
    return MediaFileData(
        fileName = getFileName(),
        mimeType = mimeType,
        fileUrl = url ?: "",
        elementToDecrypt = file,
        width = imageInfo.thumbnailInfo?.width ?: Target.SIZE_ORIGINAL,
        height = imageInfo.thumbnailInfo?.height ?: Target.SIZE_ORIGINAL,
        duration = ""
    )
}

private fun MessageVideoContent.toThumbnailFileData(): MediaFileData? {
    val videoInfo = videoInfo ?: return null
    val file = videoInfo.thumbnailFile?.toElementToDecrypt() ?: return null
    val url = videoInfo.thumbnailFile?.url ?: videoInfo.thumbnailUrl
    val mimeType = videoInfo.thumbnailInfo?.mimeType ?: ""
    return MediaFileData(
        fileName = getFileName(),
        mimeType = mimeType,
        fileUrl = url ?: "",
        elementToDecrypt = file,
        width = videoInfo.thumbnailInfo?.width ?: Target.SIZE_ORIGINAL,
        height = videoInfo.thumbnailInfo?.height ?: Target.SIZE_ORIGINAL,
        duration = ""
    )
}

private fun TimelineEvent.toThumbnailFileData(mediaType: MediaType) = root.getClearContent().let {
    when (mediaType) {
        MediaType.Image -> it.toModel<MessageImageContent>()?.toThumbnailFileData()
        MediaType.Video -> it.toModel<MessageVideoContent>()?.toThumbnailFileData()
    }
}