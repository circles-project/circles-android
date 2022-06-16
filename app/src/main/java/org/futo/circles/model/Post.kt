package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.provider.MatrixSessionProvider

sealed class Post(
    open val postInfo: PostInfo,
    open val content: PostContent
) : IdEntity<String> {
    override val id: String get() = postInfo.id
    fun isMyPost(): Boolean =
        postInfo.sender.userId == MatrixSessionProvider.currentSession?.myUserId
}

data class RootPost(
    override val postInfo: PostInfo,
    override val content: PostContent,
    val replies: List<ReplyPost> = emptyList(),
    val isRepliesVisible: Boolean = false
) : Post(postInfo, content) {
    fun hasReplies(): Boolean = replies.isNotEmpty()
    fun getRepliesCount(): Int = replies.size
}

data class ReplyPost(
    override val postInfo: PostInfo,
    override val content: PostContent,
    val replyToId: String
) : Post(postInfo, content)