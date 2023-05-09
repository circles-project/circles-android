package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.send.SendState

data class Post(
    val postInfo: PostInfo,
    val content: PostContent,
    val sendState: SendState,
    val readInfo: PostReadInfo,
) : IdEntity<String> {
    override val id: String get() = postInfo.id
    fun isMyPost(): Boolean =
        postInfo.sender.userId == MatrixSessionProvider.currentSession?.myUserId

    fun canShare(): Boolean = content.type != PostContentType.POLL_CONTENT
}