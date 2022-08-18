package org.futo.circles.feature.room.select

import androidx.lifecycle.ViewModel
import org.futo.circles.model.SelectableRoomListItem

class SelectRoomsViewModel(
    private val dataSource: SelectRoomsDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.roomsLiveData

    fun getSelectedRooms() = dataSource.getSelectedRooms()

    fun onRoomSelected(item: SelectableRoomListItem) {
        dataSource.toggleRoomSelect(item)
    }
}