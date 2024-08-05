package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.toRoomInfo
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserIdUtils
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
    selected: Boolean = false
) = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = selected
)

fun RoomSummary.toJoinedGalleryListItem() = JoinedGalleryListItem(
    id = roomId,
    info = toRoomInfo(),
    roomOwner = getRoomOwner(roomId)?.toUser(),
    knockRequestsCount = getKnocksCount(roomId)
)

fun RoomMemberSummary.toUser() = User(userId, notEmptyDisplayName(), avatarUrl)

fun RoomMemberSummary.toKnockRequestListItem(roomId: String, requestType: RoomRequestTypeArg) =
    KnockRequestListItem(
        roomId = roomId,
        roomName = MatrixSessionProvider.currentSession?.getRoom(roomId)?.roomSummary()?.nameOrId()
            ?: "",
        requestType = requestType,
        requesterId = userId,
        requesterName = displayName ?: UserIdUtils.removeDomainSuffix(userId),
        requesterAvatarUrl = avatarUrl,
        message = getReasonMessage(roomId, userId)
    )

private fun getReasonMessage(roomId: String, userId: String) =
    MatrixSessionProvider.currentSession?.getRoom(roomId)?.stateService()?.getStateEvents(
        setOf(EventType.STATE_ROOM_MEMBER), QueryStringValue.Contains(userId)
    )?.firstOrNull {
        it.content.toModel<RoomMemberContent>()?.membership == Membership.KNOCK
    }?.content.toModel<RoomMemberContent>()?.safeReason

fun RoomSummary.toRoomInviteListItem(requestType: RoomRequestTypeArg, shouldBlurIcon: Boolean) =
    if (requestType == RoomRequestTypeArg.DM) {
        val user = MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")
        val userName = user?.notEmptyDisplayName() ?: ""
        RoomInviteListItem(
            roomId = roomId,
            info = RoomInfo(userName, user?.avatarUrl ?: avatarUrl),
            inviterName = userName,
            isEncrypted = isEncrypted,
            shouldBlurIcon = shouldBlurIcon,
            requestType = requestType
        )
    } else {
        RoomInviteListItem(
            roomId = roomId,
            info = toRoomInfo(),
            inviterName = getInviterName(),
            isEncrypted = isEncrypted,
            shouldBlurIcon = shouldBlurIcon,
            requestType = requestType
        )
    }

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""