package com.futo.circles.ui.groups.timeline.model

import org.matrix.android.sdk.api.session.room.model.message.MessageType

enum class GroupMessageType(val typeKey: String) {
    TEXT_MESSAGE(MessageType.MSGTYPE_TEXT), IMAGE_MESSAGE(MessageType.MSGTYPE_IMAGE)
}