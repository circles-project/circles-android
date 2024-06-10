package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.RoomInfo

sealed class CircleListItem : IdEntity<String>
data class CirclesHeaderItem(
    val titleRes: Int
) : CircleListItem() {
    override val id: String = titleRes.toString()
}

data class JoinedCircleListItem(
    override val id: String,
    val info: RoomInfo,
    val followingCount: Int,
    val followedByCount: Int,
    val unreadCount: Int,
    val knockRequestsCount: Int
) : CircleListItem()

data class CircleInvitesNotificationListItem(
    val invitesCount: Int
) : CircleListItem() {
    override val id: String = "CircleInvitesNotificationListItem"
}

