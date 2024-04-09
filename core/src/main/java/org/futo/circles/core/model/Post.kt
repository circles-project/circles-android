package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.matrix.android.sdk.api.session.room.send.SendState

sealed class PostListItem : IdEntity<String>
data class TimelineLoadingItem(
    override val id: String = "TimelineLoadingItem"
) : PostListItem()

data class Post(
    val postInfo: PostInfo,
    val content: PostContent,
    val sendState: SendState,
    val readByCount: Int,
    val repliesCount: Int,
    val reactionsData: List<ReactionsData>,
    val timelineName: String? = null,
    val timelineOwnerName: String? = null
) : PostListItem() {
    override val id: String get() = postInfo.id
    fun isMyPost(): Boolean = postInfo.isMyPost()

    fun canShare(): Boolean = content.type != PostContentType.POLL_CONTENT
}