package com.futo.circles.mapping

import com.futo.circles.extensions.getRoomOwners
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.FollowingListItem
import com.futo.circles.model.GroupListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toGroupListItem() = GroupListItem(
    id = roomId,
    title = nameOrId(),
    topic = topic,
    membersCount = joinedMembersCount ?: 0,
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    isEncrypted = isEncrypted,
    avatarUrl = avatarUrl
)

fun RoomSummary.toCircleListItem() = CircleListItem(
    id = roomId,
    name = nameOrId(),
    followingCount = spaceChildren?.size ?: 0,
    followedByCount = getFollowersCount(),
    avatarUrl = avatarUrl
)

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.getFollowersCount(): Int {
    var followersCount = 0
    spaceChildren?.forEach { followersCount += (it.activeMemberCount ?: 0) }
    return followersCount
}

fun RoomSummary.toFollowingListItem() = FollowingListItem(
    id = roomId,
    name = nameOrId(),
    ownerName = getRoomOwners(roomId).firstOrNull()?.displayName ?: "",
    avatarUrl = avatarUrl,
    updatedTime = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis()
)