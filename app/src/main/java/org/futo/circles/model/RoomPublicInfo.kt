package org.futo.circles.model

import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.peeking.PeekResult

data class RoomPublicInfo(
    val id: String,
    val displayName: String?,
    val avatarUrl: String?,
    val topic: String,
    val memberCount: Int?,
    val alias: String?,
    val membership: Membership
)

fun RoomSummary.toRoomPublicInfo() = RoomPublicInfo(
    id = roomId,
    displayName = name,
    avatarUrl = avatarUrl,
    topic = topic,
    memberCount = joinedMembersCount,
    alias = canonicalAlias,
    membership = membership
)

fun PeekResult.Success.toRoomPublicInfo() = RoomPublicInfo(
    id = roomId,
    displayName = name,
    avatarUrl = avatarUrl,
    topic = topic ?: "",
    memberCount = numJoinedMembers,
    alias = alias,
    membership = Membership.NONE
)