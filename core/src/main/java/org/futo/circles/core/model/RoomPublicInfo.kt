package org.futo.circles.core.model

import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.peeking.PeekResult

data class RoomPublicInfo(
    val id: String,
    val name: String?,
    val avatarUrl: String?,
    val topic: String?,
    val memberCount: Int,
    val membership: Membership,
    val type: ShareUrlTypeArg
)

fun RoomSummary.toRoomPublicInfo(urlType: ShareUrlTypeArg) = RoomPublicInfo(
    id = roomId,
    name = name.takeIf { it.isNotEmpty() },
    avatarUrl = avatarUrl.takeIf { it.isNotEmpty() },
    topic = topic,
    memberCount = joinedMembersCount ?: 0,
    membership = membership,
    type = urlType
)

fun PeekResult.Success.toRoomPublicInfo(urlType: ShareUrlTypeArg) = RoomPublicInfo(
    id = roomId,
    name = name?.takeIf { it.isNotEmpty() },
    avatarUrl = avatarUrl?.takeIf { it.isNotEmpty() },
    topic = topic,
    memberCount = numJoinedMembers ?: 0,
    membership = Membership.NONE,
    type = urlType
)

