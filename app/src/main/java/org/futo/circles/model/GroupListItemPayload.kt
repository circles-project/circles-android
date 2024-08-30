package org.futo.circles.model

import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.model.RoomInfo

data class GroupListItemPayload(
    val roomInfo: RoomInfo?,
    val members: List<CirclesUserSummary>?,
    val unreadCount: Int?
)