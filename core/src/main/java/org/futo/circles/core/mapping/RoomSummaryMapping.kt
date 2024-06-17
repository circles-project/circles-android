package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getCircleAvatarUrl
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.SelectRoomTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.model.isCircle
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.core.utils.getKnocksCount
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User

fun RoomSummary.nameOrId() =
    displayName.takeIf { it.isNotEmpty() } ?: name.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toSelectableRoomListItem(
    roomTypeArg: SelectRoomTypeArg,
    selected: Boolean = false
) = SelectableRoomListItem(
    id = roomId,
    info = RoomInfo(
        nameOrId(),
        if (roomTypeArg.isCircle()) getCircleAvatarUrl() else avatarUrl
    ),
    isSelected = selected
)

fun RoomSummary.toJoinedGalleryListItem() = JoinedGalleryListItem(
    id = roomId,
    info = RoomInfo(nameOrId(), avatarUrl),
    roomOwner = getRoomOwner(roomId)?.toUser(),
    knockRequestsCount = getKnocksCount(roomId)
)

fun RoomMemberSummary.toUser() = User(userId, notEmptyDisplayName(), avatarUrl)

fun RoomMemberSummary.toKnockRequestListItem(roomId: String) = KnockRequestListItem(
    roomId = roomId,
    requesterId = userId,
    requesterName = displayName ?: UserUtils.removeDomainSuffix(userId),
    requesterAvatarUrl = avatarUrl,
    message = getReasonMessage(roomId, userId)
)

private fun getReasonMessage(roomId: String, userId: String) =
    MatrixSessionProvider.currentSession?.getRoom(roomId)?.stateService()?.getStateEvents(
        setOf(EventType.STATE_ROOM_MEMBER), QueryStringValue.Contains(userId)
    )?.firstOrNull {
        it.content.toModel<RoomMemberContent>()?.membership == Membership.KNOCK
    }?.content.toModel<RoomMemberContent>()?.safeReason

fun RoomSummary.toRoomInviteListItem(roomType: CircleRoomTypeArg, shouldBlurIcon: Boolean) =
    RoomInviteListItem(
        roomId = roomId,
        info = RoomInfo(nameOrId(), avatarUrl),
        inviterName = getInviterName(),
        isEncrypted = isEncrypted,
        shouldBlurIcon = shouldBlurIcon,
        roomType = roomType
    )

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""