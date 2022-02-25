package com.futo.circles.extensions

import com.futo.circles.mapping.toGroupListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun List<RoomSummary>.toGroupsList(tagName: String) = mapNotNull { room ->
    val isGroup = room.tags.firstOrNull { tag -> tag.name.contains(tagName) }?.let { true } ?: false
    if (isGroup) room.toGroupListItem() else null
}

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId