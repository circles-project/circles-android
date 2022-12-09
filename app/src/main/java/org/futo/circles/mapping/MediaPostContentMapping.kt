package org.futo.circles.mapping

import com.bumptech.glide.request.target.Target
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.VideoUtils
import org.futo.circles.model.MediaContent
import org.futo.circles.model.MediaContentInfo
import org.futo.circles.model.MediaFileData
import org.futo.circles.model.PostContentType
import org.matrix.android.sdk.api.session.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.matrix.android.sdk.api.session.room.model.message.getFileName
import org.matrix.android.sdk.api.session.room.model.message.getFileUrl
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

const val MediaCaptionFieldKey = "caption"


fun TimelineEvent.toMediaContent(mediaType: MediaType): MediaContent {
    val messageContentInfo = root.getClearContent().let {
        when (mediaType) {
            MediaType.Image -> it.toModel<MessageImageContent>()
                .toMediaContentInfo(getCaption())
            MediaType.Video -> it.toModel<MessageVideoContent>()
                .toMediaContentInfo(getCaption())
        }
    }
    return MediaContent(
        type = if (mediaType == MediaType.Image) PostContentType.IMAGE_CONTENT else PostContentType.VIDEO_CONTENT,
        mediaFileData = toMediaContentData(mediaType),
        mediaContentInfo = messageContentInfo
    )
}

private fun TimelineEvent.getCaption() =
    root.getClearContent()?.get(MediaCaptionFieldKey)?.toString()

private fun MessageImageContent?.toMediaContentInfo(caption: String?): MediaContentInfo {
    return MediaContentInfo(
        caption = caption,
        thumbnailUrl = this?.info?.thumbnailFile?.url ?: "",
        width = this?.info?.width ?: Target.SIZE_ORIGINAL,
        height = this?.info?.height ?: Target.SIZE_ORIGINAL,
        duration = ""
    )
}

private fun MessageVideoContent?.toMediaContentInfo(caption: String?): MediaContentInfo {
    return MediaContentInfo(
        caption = caption,
        thumbnailUrl = this?.videoInfo?.thumbnailFile?.url ?: "",
        width = this?.videoInfo?.width ?: Target.SIZE_ORIGINAL,
        height = this?.videoInfo?.height ?: Target.SIZE_ORIGINAL,
        duration = VideoUtils.getVideoDurationString(this?.videoInfo?.duration?.toLong() ?: 0L)
    )
}

private fun TimelineEvent.toMediaContentData(mediaType: MediaType): MediaFileData {
    val messageContent = root.getClearContent().let {
        when (mediaType) {
            MediaType.Image -> it.toModel<MessageImageContent>()
            MediaType.Video -> it.toModel<MessageVideoContent>()
        }
    }
    return MediaFileData(
        fileName = messageContent?.getFileName() ?: "",
        mimeType = messageContent?.mimeType ?: "",
        fileUrl = messageContent?.getFileUrl() ?: "",
        elementToDecrypt = messageContent?.encryptedFileInfo?.toElementToDecrypt(),
    )
}