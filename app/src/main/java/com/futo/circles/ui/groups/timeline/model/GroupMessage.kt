package com.futo.circles.ui.groups.timeline.model

import org.matrix.android.sdk.api.session.room.model.message.ThumbnailInfo
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

sealed class GroupMessage(
    open val id: String,
    open val sender: SenderInfo,
    open val isEncrypted: Boolean,
    open val timestamp: Long
)

data class GroupTextMessage(
    override val id: String,
    override val sender: SenderInfo,
    override val isEncrypted: Boolean,
    override val timestamp: Long,
    val message: String
) : GroupMessage(id, sender, isEncrypted, timestamp)

data class GroupImageMessage(
    override val id: String,
    override val sender: SenderInfo,
    override val isEncrypted: Boolean,
    override val timestamp: Long,
    val encryptedImageUrl: String
) : GroupMessage(id, sender, isEncrypted, timestamp)