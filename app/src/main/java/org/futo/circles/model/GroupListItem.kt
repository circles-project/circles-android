package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.model.RoomInfo

sealed class GroupListItem(
    override val id: String
) : IdEntity<String>

data class JoinedGroupListItem(
    override val id: String,
    val info: RoomInfo,
    val members: List<CirclesUserSummary>,
    val unreadCount: Int
) : GroupListItem(id)

data class GroupInvitesNotificationListItem(
    val invitesCount: Int,
    val knockRequestsCount: Int
) : GroupListItem("GroupInvitesNotificationListItem")