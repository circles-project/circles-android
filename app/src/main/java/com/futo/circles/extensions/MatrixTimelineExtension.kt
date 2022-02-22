package com.futo.circles.extensions

import com.futo.circles.ui.groups.timeline.model.GroupImageMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessageType
import com.futo.circles.ui.groups.timeline.model.GroupTextMessage
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun List<TimelineEvent>.toFilteredMessages(): List<GroupMessage> =
    filter { it.root.getClearType() == EventType.MESSAGE }.mapNotNull {
        val messageType = it.root.getClearContent()?.toModel<MessageContent>()?.msgType

        when (GroupMessageType.values().firstOrNull { it.typeKey == messageType }) {
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
