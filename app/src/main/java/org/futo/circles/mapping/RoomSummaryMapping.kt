package org.futo.circles.mapping

import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.toRoomInfo
import org.futo.circles.core.mapping.toCircleUserSummary
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getKnocksCount
import org.futo.circles.model.JoinedCircleListItem
import org.futo.circles.model.JoinedDMsListItem
import org.futo.circles.model.JoinedGroupListItem
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary


fun RoomSummary.toJoinedGroupListItem() = JoinedGroupListItem(
    id = roomId,
    info = toRoomInfo(),
    topic = topic,
    isEncrypted = isEncrypted,
    membersCount = joinedMembersCount ?: 0,
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    unreadCount = notificationCount,
    knockRequestsCount = getKnocksCount(roomId)
)

fun RoomSummary.toJoinedCircleListItem() =
    JoinedCircleListItem(
        id = roomId,
        info = toRoomInfo(),
        followersCount = joinedMembersCount?.takeIf { it > 0 }?.let { it - 1 } ?: 0,
        unreadCount = notificationCount,
        knockRequestsCount = getKnocksCount(roomId),
        timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
        owner = getRoomOwner(roomId)?.toCircleUserSummary()
    )

fun RoomSummary.toJoinedDMListItem() = JoinedDMsListItem(
    id = roomId,
    user = MatrixSessionProvider.getSessionOrThrow().getUserOrDefault(directUserId ?: "")
        .toCirclesUserSummary(),
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    unreadCount = notificationCount
)

