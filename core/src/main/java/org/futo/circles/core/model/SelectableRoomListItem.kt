package org.futo.circles.core.model

import org.futo.circles.core.list.IdEntity

data class SelectableRoomListItem(
    override val id: String,
    val info: RoomInfo,
    val isSelected: Boolean
) : IdEntity<String>