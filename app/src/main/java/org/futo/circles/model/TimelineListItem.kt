package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.list.IdEntity

sealed class TimelineListItem : IdEntity<String>

data class TimelineHeaderItem(
    val titleRes: Int
) : TimelineListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val followingHeader = TimelineHeaderItem(R.string.following)
        val othersHeader = TimelineHeaderItem(R.string.shared_circles)
    }
}

data class TimelineRoomListItem(
    override val id: String,
    val info: RoomInfo,
    val isJoined: Boolean
) : TimelineListItem()


