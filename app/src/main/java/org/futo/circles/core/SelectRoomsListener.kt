package org.futo.circles.core

import org.futo.circles.model.SelectableRoomListItem

interface SelectRoomsListener {
    fun onRoomsSelected(rooms: List<SelectableRoomListItem>)
}