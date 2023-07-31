package org.futo.circles.core.mapping

import com.bumptech.glide.request.target.Target
import org.futo.circles.core.MediaCaptionFieldKey
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaContentInfo
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

private fun TimelineEvent.getCaption(): String? {
    val lastContent =
        annotations?.editSummary?.latestEdit?.getClearContent() ?: root.getClearContent()
    return lastContent?.get(MediaCaptionFieldKey)?.toString()
}

private fun MessageImageContent?.toMediaContentInfo(caption: String?): MediaContentInfo =
    MediaContentInfo(
        caption = caption,
        thumbnailUrl = this?.info?.thumbnailFile?.url ?: "",
        width = this?.info?.width ?: Target.SIZE_ORIGINAL,
        height = this?.info?.height ?: Target.SIZE_ORIGINAL,
        duration = "",
        thumbHash = this?.info?.blurHash
    )

private fun MessageVideoContent?.toMediaContentInfo(caption: String?): MediaContentInfo =
    MediaContentInfo(
        caption = caption,
        thumbnailUrl = this?.videoInfo?.thumbnailFile?.url ?: "",
        width = this?.videoInfo?.width ?: Target.SIZE_ORIGINAL,
        height = this?.videoInfo?.height ?: Target.SIZE_ORIGINAL,
        duration = VideoUtils.getVideoDurationString(this?.videoInfo?.duration?.toLong() ?: 0L),
        thumbHash = this?.videoInfo?.blurHash
    )

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