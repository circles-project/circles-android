package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.mapping.nameOrId
import org.matrix.android.sdk.api.session.room.model.RoomSummary

data class FilterTimelinesListItem(
    override val id: String,
    val name: String,
    val ownerName: String,
    val avatarUrl: String,
    val isSelected: Boolean
) : IdEntity<String>

fun RoomSummary.toFilterTimelinesListItem(isSelected: Boolean = true) = FilterTimelinesListItem(
    id = roomId,
    name = nameOrId(),
    ownerName = getRoomOwner(roomId)?.displayName ?: "",
    avatarUrl = avatarUrl,
    isSelected = isSelected
)