package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity


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
    val reactionsData: List<ReactionsData>
) : DmTimelineListItem() {
    override val id: String get() = info.id
}

fun Post.toDmTimelineMessage() = DmTimelineMessage(
    postInfo, content, reactionsData
)

