package org.futo.circles.model

import org.futo.circles.core.mapping.nameOrId
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.peeking.PeekResult

data class RoomPublicInfo(
    val id: String,
    val displayName: String,
    val avatarUrl: String?,
    val topic: String?,
    val memberCount: Int,
    val membership: Membership
)

fun RoomSummary.toRoomPublicInfo() = RoomPublicInfo(
    id = roomId,
    displayName = nameOrId(),
    avatarUrl = avatarUrl,
    topic = topic,
    memberCount = joinedMembersCount ?: 0,
    membership = membership
)

fun PeekResult.Success.toRoomPublicInfo() = RoomPublicInfo(
    id = roomId,
    displayName = name?.takeIf { it.isNotEmpty() } ?: roomId,
    avatarUrl = avatarUrl,
    topic = topic,
    memberCount = numJoinedMembers ?: 0,
    membership = Membership.NONE
)