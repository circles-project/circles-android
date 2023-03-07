package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.model.Membership

sealed class CircleListItem : IdEntity<String>
data class CirclesHeaderItem(
    val titleRes: Int
) : CircleListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val requestsCirclesHeader = CirclesHeaderItem(R.string.requests)
        val invitesCirclesHeader = CirclesHeaderItem(R.string.invites)
        val sharedCirclesHeader = CirclesHeaderItem(R.string.shared_circles)
        val privateCirclesHeader = CirclesHeaderItem(R.string.private_circles)
    }
}

sealed class CircleRoomListItem(
    override val id: String,
    open val info: RoomInfo,
    open val membership: Membership
) : CircleListItem()

data class JoinedCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val isShared: Boolean,
    val followingCount: Int,
    val followedByCount: Int,
    val unreadCount: Int
) : CircleRoomListItem(id, info, Membership.JOIN)

data class InvitedCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val inviterName: String,
) : CircleRoomListItem(id, info, Membership.INVITE)

data class RequestCircleListItem(
    override val id: String,
    override val info: RoomInfo,
    val requesterName: String,
) : CircleRoomListItem(id, info, Membership.KNOCK)

