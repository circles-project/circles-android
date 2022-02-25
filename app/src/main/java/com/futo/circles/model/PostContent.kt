package com.futo.circles.model

import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.internal.crypto.attachments.ElementToDecrypt

enum class PostContentType(val typeKey: String) {
    TEXT_CONTENT(MessageType.MSGTYPE_TEXT), IMAGE_CONTENT(MessageType.MSGTYPE_IMAGE)
}

sealed class PostContent(val type: PostContentType)

data class TextContent(
    val message: String
) : PostContent(PostContentType.TEXT_CONTENT)

data class ImageContent(
    val fileName: String,
    val mimeType: String,
    val fileUrl: String,
    val thumbnailUrl: String,
    val elementToDecrypt: ElementToDecrypt?,
    val width: Int,
    val height: Int
) : PostContent(PostContentType.IMAGE_CONTENT)