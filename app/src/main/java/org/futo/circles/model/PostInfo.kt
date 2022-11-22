package org.futo.circles.model

import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

data class PostInfo(
    val id: String,
    val roomId: String,
    val sender: SenderInfo,
    val isEncrypted: Boolean,
    val timestamp: Long,
    val reactionsData: List<ReactionsData>,
    val isEdited: Boolean,
    val isReadByMe: Boolean,
    val sendState: SendState,
    val readByCount: Int
)