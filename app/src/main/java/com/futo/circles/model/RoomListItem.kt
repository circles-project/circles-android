package com.futo.circles.model

import com.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class RoomListItem(
    override val id: String,
    open val info: RoomInfo,
    open val membership: Membership
) : IdEntity<String>

data class RoomInfo(
    val title: String,
    val avatarUrl: String
)

data class JoinedGroupListItem(
    override val id: String,
    override val info: RoomInfo,
    val topic: String,
    val membersCount: Int,
    val isEncrypted: Boolean,
    val timestamp: Long
) : RoomListItem(id, info, Membership.JOIN)

data class InvitedGroupListItem(
    override val id: String,
    override val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
) : RoomListItem(id, info, Membership.INVITE)

data class JoinedCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val followingCount: Int,
    val followedByCount: Int
) : RoomListItem(id, info, Membership.JOIN)

data class InvitedCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val inviterName: String,
) : RoomListItem(id, info, Membership.INVITE)

data class GalleryListItem(
    override val id: String,
    override val info: RoomInfo
) : RoomListItem(id, info, Membership.JOIN)