package com.futo.circles.ui.groups.timeline.model

import org.matrix.android.sdk.api.session.room.sender.SenderInfo

interface GroupMessage {
    val generalMessageInfo: GroupGeneralMessageInfo
    val type: GroupMessageType
}

data class GroupGeneralMessageInfo(
    val id: String,
    val sender: SenderInfo,
    val isEncrypted: Boolean,
    val timestamp: Long,
    val isReply: Boolean
)

data class GroupTextMessage(
    override val generalMessageInfo: GroupGeneralMessageInfo,
    override val type: GroupMessageType = GroupMessageType.TEXT_MESSAGE,
    val message: String,
) : GroupMessage

data class GroupImageMessage(
    override val generalMessageInfo: GroupGeneralMessageInfo,
    override val type: GroupMessageType = GroupMessageType.TEXT_MESSAGE,
    val encryptedImageUrl: String
) : GroupMessage