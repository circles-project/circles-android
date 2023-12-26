package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary

sealed class InviteListItem(
    override val id: String,
    open val inviteType: InviteTypeArg
) : IdEntity<String>

data class RoomInviteListItem(
    val roomId: String,
    override val inviteType: InviteTypeArg,
    val info: RoomInfo,
    val isEncrypted: Boolean,
    val inviterName: String,
    val shouldBlurIcon: Boolean
) : InviteListItem(roomId, inviteType)

data class FollowRequestListItem(
    val user: CirclesUserSummary,
    val reasonMessage: String?
) : InviteListItem(user.id, InviteTypeArg.People)

fun RoomSummary.toRoomInviteListItem(inviteType: InviteTypeArg, shouldBlurIcon: Boolean) =
    RoomInviteListItem(
        roomId = roomId,
        info = toRoomInfo(),
        inviterName = getInviterName(),
        isEncrypted = isEncrypted,
        shouldBlurIcon = shouldBlurIcon,
        inviteType = inviteType
    )

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""