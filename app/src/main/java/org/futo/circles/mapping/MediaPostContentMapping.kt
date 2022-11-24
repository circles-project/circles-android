package org.futo.circles.mapping

import com.bumptech.glide.request.target.Target
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.VideoUtils
import org.futo.circles.model.ImageContent
import org.futo.circles.model.MediaContentData
import org.futo.circles.model.VideoContent
import org.matrix.android.sdk.api.session.crypto.attachments.toElementToDecrypt
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageVideoContent
import org.matrix.android.sdk.api.session.room.model.message.getFileName
import org.matrix.android.sdk.api.session.room.model.message.getFileUrl
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

const val MediaCaptionFieldKey = "caption"

private fun TimelineEvent.getCaption(): String? =
    root.getClearContent()?.get(MediaCaptionFieldKey)?.toString()

fun TimelineEvent.toImageContent(): ImageContent {
    val messageContent = root.getClearContent().toModel<MessageImageContent>()

    return ImageContent(
        caption = getCaption(),
        mediaContentData = toMediaContentData(MediaType.Image),
        thumbnailUrl = messageContent?.info?.thumbnailFile?.url ?: "",
        width = messageContent?.info?.width ?: Target.SIZE_ORIGINAL,
        height = messageContent?.info?.height ?: Target.SIZE_ORIGINAL
    )
}

fun TimelineEvent.toVideoContent(): VideoContent {
    val messageContent = root.getClearContent().toModel<MessageVideoContent>()

    return VideoContent(
        caption = getCaption(),
        mediaContentData = toMediaContentData(MediaType.Video),
        thumbnailUrl = messageContent?.videoInfo?.thumbnailFile?.url ?: "",
        width = messageContent?.videoInfo?.width ?: Target.SIZE_ORIGINAL,
        height = messageContent?.videoInfo?.height ?: Target.SIZE_ORIGINAL,
        duration = VideoUtils.getVideoDurationString(
            messageContent?.videoInfo?.duration?.toLong() ?: 0L
        )
    )
}

private fun TimelineEvent.toMediaContentData(mediaType: MediaType): MediaContentData {
    val messageContent = root.getClearContent().let {
        when (mediaType) {
            MediaType.Image -> it.toModel<MessageImageContent>()
            MediaType.Video -> it.toModel<MessageVideoContent>()
        }
    }
    return MediaContentData(
        fileName = messageContent?.getFileName() ?: "",
        mimeType = messageContent?.mimeType ?: "",
        fileUrl = messageContent?.getFileUrl() ?: "",
        elementToDecrypt = messageContent?.encryptedFileInfo?.toElementToDecrypt(),
    )
}