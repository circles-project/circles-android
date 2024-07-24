package org.futo.circles.core.model

import org.futo.circles.core.R
import org.futo.circles.core.base.list.IdEntity

sealed class RoomRequestListItem : IdEntity<String>

data class RoomRequestHeaderItem(
    val titleRes: Int
) : RoomRequestListItem() {
    override val id: String = titleRes.toString()

    companion object {
        val invitesHeader = RoomRequestHeaderItem(R.string.invites)
        val requestForInviteHeader = RoomRequestHeaderItem(R.string.requests_for_invitation)
    }
}

data class RoomInviteListItem(
    val roomId: String,
    val requestType: RoomRequestTypeArg,
    val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
    val shouldBlurIcon: Boolean,
    val isLoading: Boolean = false
) : RoomRequestListItem() {
    override val id: String = roomId
}

data class KnockRequestListItem(
    val roomId: String,
    val roomName: String,
    val requestType: RoomRequestTypeArg,
    val requesterId: String,
    val requesterName: String,
    val requesterAvatarUrl: String?,
    val message: String?,
    val isLoading: Boolean = false
) : RoomRequestListItem() {
    override val id: String = roomId
}

fun KnockRequestListItem.toCircleUser() = CirclesUserSummary(
    requesterId, requesterName, requesterAvatarUrl ?: ""
)