package org.futo.circles.feature.room.select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.model.SelectableRoomListItem
import javax.inject.Inject

@HiltViewModel
class SelectRoomsViewModel @Inject constructor(
    private val dataSource: SelectRoomsDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.roomsFlow.asLiveData()

    fun getSelectedRooms() = dataSource.getSelectedRooms()

    fun onRoomSelected(item: SelectableRoomListItem) {
        dataSource.toggleRoomSelect(item)
    }
}