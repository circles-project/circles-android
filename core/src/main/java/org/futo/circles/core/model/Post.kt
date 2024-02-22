package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.matrix.android.sdk.api.session.room.send.SendState

data class Post(
    val postInfo: PostInfo,
    val content: PostContent,
    val sendState: SendState,
    val readByCount: Int,
    val repliesCount: Int,
    val reactionsData: List<ReactionsData>,
    val timelineName: String? = null
) : IdEntity<String> {
    override val id: String get() = postInfo.id
    fun isMyPost(): Boolean = postInfo.isMyPost()

    fun canShare(): Boolean = content.type != PostContentType.POLL_CONTENT
}