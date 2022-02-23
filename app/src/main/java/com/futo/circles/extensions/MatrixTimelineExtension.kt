package com.futo.circles.extensions

import com.futo.circles.ui.groups.timeline.model.*
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.isReply

fun List<TimelineEvent>.toFilteredMessages(): List<GroupMessage> =
    filter { it.root.getClearType() == EventType.MESSAGE }.mapNotNull {
        val messageType = it.root.getClearContent()?.toModel<MessageContent>()?.msgType

        when (GroupMessageType.values().firstOrNull { it.typeKey == messageType }) {
            GroupMessageType.TEXT_MESSAGE -> it.toTextMessage()
            GroupMessageType.IMAGE_MESSAGE -> it.toImageMessage()
            else -> null
        }
    }

fun TimelineEvent.toGeneralMessageInfo(): GroupGeneralMessageInfo = GroupGeneralMessageInfo(
    id = eventId,
    isEncrypted = isEncrypted(),
    timestamp = root.originServerTs ?: System.currentTimeMillis(),
    sender = senderInfo,
    isReply = isReply()
)

fun TimelineEvent.toTextMessage(): GroupTextMessage = GroupTextMessage(
    generalMessageInfo = toGeneralMessageInfo(),
    message = root.getClearContent().toModel<MessageTextContent>()?.body ?: ""
)

fun TimelineEvent.toImageMessage(): GroupImageMessage = GroupImageMessage(
    generalMessageInfo = toGeneralMessageInfo(),
    encryptedImageUrl = root.getClearContent()
        .toModel<MessageImageContent>()?.info?.thumbnailFile?.url ?: ""
)
