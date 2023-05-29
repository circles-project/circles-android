package org.futo.circles.base

import org.futo.circles.model.SelectableRoomListItem

interface RoomsListener {
    fun onRoomsListChanged(rooms: List<SelectableRoomListItem>)
}