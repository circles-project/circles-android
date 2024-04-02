package org.futo.circles.core.model

import org.futo.circles.core.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.mapping.toRoomInfo
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo

sealed class TimelineListItem : IdEntity<String>

data class TimelineHeaderItem(
    val titleRes: Int
) : TimelineListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val followingHeader = TimelineHeaderItem(R.string.following)
        val othersHeader = TimelineHeaderItem(R.string.others)
        val mutualFriends = TimelineHeaderItem(R.string.mutual_friends)
    }
}

data class TimelineRoomListItem(
    override val id: String,
    val info: RoomInfo,
    val isJoined: Boolean,
    val isLoading: Boolean = false
) : TimelineListItem()

data class MutualFriendListItem(
    val user: CirclesUserSummary,
    val isIgnored: Boolean
) : TimelineListItem() {
    override val id: String = user.id
}

fun RoomSummary.toTimelineRoomListItem() = TimelineRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isJoined = membership == Membership.JOIN
)

fun SpaceChildInfo.toTimelineRoomListItem() = TimelineRoomListItem(
    id = childRoomId,
    info = RoomInfo(
        title = name?.takeIf { it.isNotEmpty() } ?: childRoomId,
        avatarUrl = avatarUrl ?: ""
    ),
    isJoined = false
)


