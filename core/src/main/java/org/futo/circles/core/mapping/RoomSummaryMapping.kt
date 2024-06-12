package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getCircleAvatarUrl
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.SelectableRoomListItem
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User

fun RoomSummary.nameOrId() =
    displayName.takeIf { it.isNotEmpty() } ?: name.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toSelectableRoomListItem(
    roomTypeArg: CircleRoomTypeArg,
    selected: Boolean = false
) = SelectableRoomListItem(
    id = roomId,
    info = RoomInfo(
        nameOrId(),
        if (roomTypeArg == CircleRoomTypeArg.Circle) getCircleAvatarUrl() else avatarUrl
    ),
    isSelected = selected
)

fun RoomSummary.toJoinedGalleryListItem() = JoinedGalleryListItem(
    id = roomId,
    info = RoomInfo(nameOrId(), avatarUrl),
    roomOwner = getRoomOwner(roomId)?.toUser()
)

fun RoomMemberSummary.toUser() = User(userId, notEmptyDisplayName(), avatarUrl)