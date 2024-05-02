package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.RoomInfo

sealed class CircleListItem : IdEntity<String>
data class CirclesHeaderItem(
    val titleRes: Int
) : CircleListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val sharedCirclesHeader = CirclesHeaderItem(R.string.shared_circles)
        val privateCirclesHeader = CirclesHeaderItem(R.string.private_circles)
    }
}

data class JoinedCircleListItem(
    override val id: String,
    val info: RoomInfo,
    val isShared: Boolean,
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

