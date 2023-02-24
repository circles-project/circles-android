package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class CircleListItem(
    override val id: String,
    open val info: RoomInfo,
    open val membership: Membership
) : IdEntity<String>

data class JoinedCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val followingCount: Int,
    val followedByCount: Int,
    val unreadCount: Int
) : CircleListItem(id, info, Membership.JOIN)

data class InvitedCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val inviterName: String,
) : CircleListItem(id, info, Membership.INVITE)

