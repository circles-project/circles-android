package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.model.RoomInfo

sealed class CircleListItem : IdEntity<String>

data class CirclesHeaderItem(
    val titleRes: Int
) : CircleListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val myCirclesHeader = CirclesHeaderItem(org.futo.circles.R.string.my_circles)
        val circlesIamFollowingHeader =
            CirclesHeaderItem(org.futo.circles.R.string.circles_i_am_following)
    }
}

data object AllCirclesListItem : CircleListItem() {
    override val id: String = "AllCirclesListItem"
}

data class JoinedCircleListItem(
    override val id: String,
    val info: RoomInfo,
    val owner: CirclesUserSummary?,
    val followersCount: Int,
    val unreadCount: Int,
    val knockRequestsCount: Int,
    val timestamp: Long
) : CircleListItem()

data class CircleInvitesNotificationListItem(
    val invitesCount: Int,
    val knocksCount: Int
) : CircleListItem() {
    override val id: String = "CircleInvitesNotificationListItem"
}
