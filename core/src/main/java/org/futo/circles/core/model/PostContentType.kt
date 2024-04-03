package org.futo.circles.core.model

import org.matrix.android.sdk.api.session.room.model.message.MessageType

enum class PostContentType(val typeKey: String) {
    TEXT_CONTENT(MessageType.MSGTYPE_TEXT),
    IMAGE_CONTENT(MessageType.MSGTYPE_IMAGE),
    VIDEO_CONTENT(MessageType.MSGTYPE_VIDEO),
    POLL_CONTENT(MessageType.MSGTYPE_POLL_START),
    OTHER_CONTENT("other_content")
}