package org.futo.circles.model

import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt
import org.matrix.android.sdk.api.session.room.model.message.MessageType

enum class PostContentType(val typeKey: String) {
    TEXT_CONTENT(MessageType.MSGTYPE_TEXT),
    IMAGE_CONTENT(MessageType.MSGTYPE_IMAGE),
    VIDEO_CONTENT(MessageType.MSGTYPE_VIDEO)
}

sealed class PostContent(val type: PostContentType) {
    fun isMedia(): Boolean =
        type == PostContentType.IMAGE_CONTENT || type == PostContentType.VIDEO_CONTENT
}

data class TextContent(
    val message: String
) : PostContent(PostContentType.TEXT_CONTENT)

data class ImageContent(
    val mediaContentData: MediaContentData,
    val thumbnailUrl: String,
    val width: Int,
    val height: Int
) : PostContent(PostContentType.IMAGE_CONTENT) {
    val aspectRatio = width.toFloat() / height.toFloat()
}

data class VideoContent(
    val mediaContentData: MediaContentData,
    val thumbnailUrl: String,
    val width: Int,
    val height: Int,
    val duration: String
) : PostContent(PostContentType.VIDEO_CONTENT) {
    val aspectRatio = width.toFloat() / height.toFloat()
}

data class MediaContentData(
    val fileName: String,
    val mimeType: String,
    val fileUrl: String,
    val elementToDecrypt: ElementToDecrypt?
)