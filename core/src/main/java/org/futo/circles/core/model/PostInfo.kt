package org.futo.circles.core.model

import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.sender.SenderInfo

data class PostInfo(
    val id: String,
    val roomId: String,
    val sender: SenderInfo,
    val isEncrypted: Boolean,
    val timestamp: Long,
    val isEdited: Boolean,
    val editTimestamp: Long?
) {
    fun isMyPost(): Boolean =
        sender.userId == MatrixSessionProvider.currentSession?.myUserId

    fun getLastModifiedTimestamp() = editTimestamp ?: timestamp
}