package com.futo.circles.mapping

import com.futo.circles.extensions.getTimelineRoomFor
import com.futo.circles.model.CircleInfo
import com.futo.circles.model.InvitedCircleListItem
import com.futo.circles.model.JoinedCircleListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toJoinedCircleListItem() = JoinedCircleListItem(
    id = roomId,
    info = toCircleInfo(),
    followingCount = spaceChildren?.size?.takeIf { it != 0 }?.minus(1) ?: 0,
    followedByCount = getFollowersCount(),
)

fun RoomSummary.toInviteCircleListItem() = InvitedCircleListItem(
    id = roomId,
    info = toCircleInfo(),
    inviterName = MatrixSessionProvider.currentSession?.getUser(inviterId ?: "")?.displayName ?: ""
)

fun RoomSummary.toCircleInfo() = CircleInfo(
    title = nameOrId(),
    avatarUrl = avatarUrl
)

fun RoomSummary.getFollowersCount(): Int =
    getTimelineRoomFor(roomId)?.roomSummary()?.otherMemberIds?.size ?: 0