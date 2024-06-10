package org.futo.circles.mapping

import org.futo.circles.core.extensions.getCircleAvatarUrl
import org.futo.circles.core.extensions.getPowerLevelContent
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.model.JoinedCircleListItem
import org.futo.circles.model.JoinedGroupListItem
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
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
        info = RoomInfo(nameOrId(), getCircleAvatarUrl()),
        followingCount = getFollowingCount(),
        followedByCount = getFollowersCount(),
        unreadCount = getCircleUnreadMessagesCount(),
        knockRequestsCount = getKnocksCount(getTimelineRoomFor(roomId)?.roomId ?: "")
    )

private fun RoomSummary.getFollowingCount() = spaceChildren?.filter {
    getJoinedRoomById(it.childRoomId) != null &&
            getRoomOwner(it.childRoomId)?.userId != MatrixSessionProvider.currentSession?.myUserId
}?.size ?: 0

//Joined members minus room owner
private fun RoomSummary.getFollowersCount(): Int {
    val joinedToTimelineCount = getTimelineRoomFor(roomId)?.roomSummary()?.joinedMembersCount ?: 0
    val followersCount = joinedToTimelineCount.takeIf { it > 0 }?.let { it - 1 } ?: 0
    return followersCount
}


private fun RoomSummary.getCircleUnreadMessagesCount(): Int {
    var unreadInCircle = 0
    spaceChildren?.forEach {
        val unreadInChildRoom =
            MatrixSessionProvider.currentSession?.getRoomSummary(it.childRoomId)?.notificationCount
                ?: 0
        unreadInCircle += unreadInChildRoom
    }
    return unreadInCircle
}

fun getKnocksCount(roomId: String): Int {
    if (getPowerLevelContent(roomId)?.isCurrentUserAbleToInvite() == false) return 0
    return MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
        ?.getRoomMembers(
            roomMemberQueryParams {
                excludeSelf = true
                memberships = listOf(Membership.KNOCK)
            }
        )?.size ?: 0
}

