package com.futo.circles.model

import org.matrix.android.sdk.api.session.room.model.message.MessageType

enum class PostContentType(val typeKey: String) {
    TEXT_CONTENT(MessageType.MSGTYPE_TEXT), IMAGE_CONTENT(MessageType.MSGTYPE_IMAGE)
}

sealed class PostContent(val type: PostContentType)

data class TextContent(
    val message: String
) : PostContent(PostContentType.TEXT_CONTENT)

data class ImageContent(
    val url: String
) : PostContent(PostContentType.IMAGE_CONTENT)