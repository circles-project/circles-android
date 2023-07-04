package org.futo.circles.core.mapping

import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.model.SelectableRoomListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toRoomInfo() = RoomInfo(
    title = nameOrId(),
    avatarUrl = avatarUrl
)

fun RoomSummary.toSelectableRoomListItem(selected: Boolean = false) = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = selected
)