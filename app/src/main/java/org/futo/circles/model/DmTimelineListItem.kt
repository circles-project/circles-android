package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostInfo
import org.futo.circles.core.model.ReactionsData


sealed class DmTimelineListItem : IdEntity<String>

data class DmTimelineLoadingItem(
    override val id: String = "DmLoadingItem"
) : DmTimelineListItem()

data class DmTimelineTimeHeaderItem(
    val date: Long
) : DmTimelineListItem() {
    override val id: String get() = date.toString()
}

data class DmTimelineMessage(
    val info: PostInfo,
    val content: PostContent,
    val reactionsData: List<ReactionsData>,
    val shapeType: DmShapeType
) : DmTimelineListItem() {
    override val id: String get() = info.id
    fun isMyMessage(): Boolean = info.isMyPost()
}

fun Post.toDmTimelineMessage() = DmTimelineMessage(
    postInfo, content, reactionsData, DmShapeType.Single
)

