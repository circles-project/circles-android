package org.futo.circles.base

import org.futo.circles.core.model.SelectableRoomListItem

interface RoomsListener {
    fun onRoomsListChanged(rooms: List<SelectableRoomListItem>)
}