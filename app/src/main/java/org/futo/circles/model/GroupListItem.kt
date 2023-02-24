package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class GroupListItem(
    override val id: String,
    open val info: RoomInfo,
    open val membership: Membership
) : IdEntity<String>

data class JoinedGroupListItem(
    override val id: String,
    override val info: RoomInfo,
    val topic: String,
    val membersCount: Int,
    val isEncrypted: Boolean,
    val timestamp: Long,
    val unreadCount: Int
) : GroupListItem(id, info, Membership.JOIN)

data class InvitedGroupListItem(
    override val id: String,
    override val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
) : GroupListItem(id, info, Membership.INVITE)