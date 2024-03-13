package org.futo.circles.core.model

import org.futo.circles.core.R
import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary

sealed class InviteListItem(
    override val id: String
) : IdEntity<String>


data class InviteHeader(
    val titleRes: Int
) : InviteListItem(titleRes.toString()) {
    companion object {
        val connectInvitesHeader = InviteHeader(R.string.invites)
        val followRequestHeader = InviteHeader(R.string.requests)
    }
}

data class RoomInviteListItem(
    val roomId: String,
    val roomType: CircleRoomTypeArg,
    val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
    val shouldBlurIcon: Boolean,
    val isLoading: Boolean = false
) : InviteListItem(roomId)

data class FollowRequestListItem(
    val user: CirclesUserSummary,
    val reasonMessage: String?,
    val isLoading: Boolean = false
) : InviteListItem(user.id)

data class ConnectionInviteListItem(
    val roomId: String,
    val user: CirclesUserSummary,
    val isLoading: Boolean = false
) : InviteListItem(roomId)

fun RoomSummary.toRoomInviteListItem(roomType: CircleRoomTypeArg, shouldBlurIcon: Boolean) =
    RoomInviteListItem(
        roomId = roomId,
        info = toRoomInfo(),
        inviterName = getInviterName(),
        isEncrypted = isEncrypted,
        shouldBlurIcon = shouldBlurIcon,
        roomType = roomType
    )

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""