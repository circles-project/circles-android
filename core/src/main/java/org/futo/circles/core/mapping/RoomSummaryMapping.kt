package org.futo.circles.core.mapping

import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.SelectableRoomListItem
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User

fun RoomSummary.nameOrId() =
    displayName.takeIf { it.isNotEmpty() } ?: name.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toRoomInfo() = RoomInfo(
    title = nameOrId(),
    avatarUrl = avatarUrl
)

fun RoomSummary.toSelectableRoomListItem(selected: Boolean = false) = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = selected
)

fun RoomSummary.toJoinedGalleryListItem() = JoinedGalleryListItem(
    id = roomId,
    info = toRoomInfo(),
    roomOwner = getRoomOwner(roomId)?.toUser()
)

fun RoomMemberSummary.toUser() = User(userId, notEmptyDisplayName(), avatarUrl)