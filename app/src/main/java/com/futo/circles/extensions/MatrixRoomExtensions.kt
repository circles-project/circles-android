package com.futo.circles.extensions

import com.futo.circles.core.GROUP_TYPE
import com.futo.circles.mapping.toGroupListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun List<RoomSummary>.toGroupsList() = mapNotNull { room ->
    val isGroup = room.roomType == GROUP_TYPE
    if (isGroup) room.toGroupListItem() else null
}

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId