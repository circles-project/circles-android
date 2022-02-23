package com.futo.circles.mapping

import com.futo.circles.ui.groups.timeline.model.GroupGeneralMessageInfo
import com.futo.circles.ui.groups.timeline.model.GroupImageMessage
import com.futo.circles.ui.groups.timeline.model.GroupTextMessage
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageImageContent
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getRelationContent

fun TimelineEvent.toGeneralMessageInfo(isRepliesVisible: Boolean): GroupGeneralMessageInfo =
    GroupGeneralMessageInfo(
        id = eventId,
        isEncrypted = isEncrypted(),
        timestamp = root.originServerTs ?: System.currentTimeMillis(),
        sender = senderInfo,
        relationId = getRelationContent()?.inReplyTo?.eventId,
        isRepliesVisible = isRepliesVisible
    )

fun TimelineEvent.toTextMessage(isRepliesVisible: Boolean): GroupTextMessage = GroupTextMessage(
    generalMessageInfo = toGeneralMessageInfo(isRepliesVisible),
    message = root.getClearContent().toModel<MessageTextContent>()?.body ?: ""
)

fun TimelineEvent.toImageMessage(isRepliesVisible: Boolean): GroupImageMessage = GroupImageMessage(
    generalMessageInfo = toGeneralMessageInfo(isRepliesVisible),
    encryptedImageUrl = root.getClearContent()
        .toModel<MessageImageContent>()?.info?.thumbnailFile?.url ?: ""
)
