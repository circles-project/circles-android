package org.futo.circles.core.feature.room.select.interfaces

import org.futo.circles.core.model.SelectableRoomListItem

interface SelectRoomsListener {
    fun onRoomsSelected(rooms: List<SelectableRoomListItem>)
}