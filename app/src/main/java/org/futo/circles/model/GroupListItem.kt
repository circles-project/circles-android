package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.RoomInfo

sealed class GroupListItem(
    override val id: String
) : IdEntity<String>

data class JoinedGroupListItem(
    override val id: String,
    val info: RoomInfo,
    val topic: String,
    val membersCount: Int,
    val knockRequestsCount: Int,
    val isEncrypted: Boolean,
    val timestamp: Long,
    val unreadCount: Int
) : GroupListItem(id)

data class GroupInvitesNotificationListItem(
    val invitesCount: Int
) : GroupListItem("GroupInvitesNotificationListItem")