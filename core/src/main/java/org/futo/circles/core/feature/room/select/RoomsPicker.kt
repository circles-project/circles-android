package org.futo.circles.core.feature.room.select

import org.futo.circles.core.model.SelectableRoomListItem

interface RoomsPicker {
    var selectRoomsListener: SelectRoomsListener?
    fun getSelectedRooms(): List<SelectableRoomListItem>
}