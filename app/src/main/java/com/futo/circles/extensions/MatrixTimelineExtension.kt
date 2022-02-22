package com.futo.circles.extensions

import com.futo.circles.ui.groups.timeline.model.*
import com.futo.circles.utils.ROOM_MESSAGE_TYPE
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun List<TimelineEvent>.toFilteredMessages(): List<GroupMessage> =
    filter { it.root.getClearType() == ROOM_MESSAGE_TYPE }.mapNotNull {
        val messageType = it.root.getClearContent()?.toModel<MessageContent>()?.msgType
        when (safeValueOf<GroupMessageType>(messageType)) {
            GroupMessageType.TEXT_MESSAGE -> it.toTextMessage()
            GroupMessageType.IMAGE_MESSAGE -> it.toImageMessage()
            else -> null
        }
    }

fun TimelineEvent.toTextMessage(): GroupTextMessage = GroupTextMessage(
    id = eventId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    message = root.getClearContent().toModel<MessageTextContent>()?.body ?: ""
)

fun TimelineEvent.toImageMessage(): GroupImageMessage = GroupImageMessage(
    id = eventId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    encryptedImageUrl = root.getClearContent()
        .toModel<MessageImageContent>()?.info?.thumbnailFile?.url ?: ""
)
