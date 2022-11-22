package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.send.SendState

sealed class Post(
    open val postInfo: PostInfo,
    open val content: PostContent,
    open val sendState: SendState,
    open val readInfo: PostReadInfo,
) : IdEntity<String> {
    override val id: String get() = postInfo.id
    fun isMyPost(): Boolean =
        postInfo.sender.userId == MatrixSessionProvider.currentSession?.myUserId

    fun canShare(): Boolean = content.type != PostContentType.POLL_CONTENT
}

data class RootPost(
    override val postInfo: PostInfo,
    override val content: PostContent,
    override val sendState: SendState,
    override val readInfo: PostReadInfo,
    val replies: List<ReplyPost> = emptyList(),
    val isRepliesVisible: Boolean = false
) : Post(postInfo, content, sendState, readInfo) {
    fun hasReplies(): Boolean = replies.isNotEmpty()
    fun getRepliesCount(): Int = replies.size
}

data class ReplyPost(
    override val postInfo: PostInfo,
    override val content: PostContent,
    override val sendState: SendState,
    override val readInfo: PostReadInfo,
    val replyToId: String
) : Post(postInfo, content, sendState, readInfo)