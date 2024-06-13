package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getCircleAvatarUrl
import org.futo.circles.core.extensions.getPowerLevelContent
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.SelectRoomTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.model.isCircle
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
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

fun getKnocksCount(roomId: String): Int {
    if (getPowerLevelContent(roomId)?.isCurrentUserAbleToInvite() == false) return 0
    return MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
        ?.getRoomMembers(
            roomMemberQueryParams {
                excludeSelf = true
                memberships = listOf(Membership.KNOCK)
            }
        )?.size ?: 0
}