package org.futo.circles.core

import org.futo.circles.model.SelectableRoomListItem

interface RoomsListener {
    fun onRoomsListChanged(rooms: List<SelectableRoomListItem>)
}