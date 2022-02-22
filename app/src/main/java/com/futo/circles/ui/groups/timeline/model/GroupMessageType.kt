package com.futo.circles.ui.groups.timeline.model

import org.matrix.android.sdk.api.session.room.model.message.MessageType
import java.lang.Enum.valueOf

enum class GroupMessageType(val typeKey: String) {
    TEXT_MESSAGE(MessageType.MSGTYPE_TEXT), IMAGE_MESSAGE(MessageType.MSGTYPE_IMAGE)
}

inline fun <reified T : Enum<T>> safeValueOf(type: String?): T? {
    return try {
        valueOf(T::class.java, type ?: "")
    } catch (e: IllegalArgumentException) {
        null
    }
}