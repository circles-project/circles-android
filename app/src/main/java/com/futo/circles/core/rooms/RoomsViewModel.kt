package com.futo.circles.core.rooms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.rooms.data_source.RoomsDataSource
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.model.RoomListItem


abstract class RoomsViewModel(
    private val dataSource: RoomsDataSource
) : ViewModel() {

    abstract val roomsLiveData: LiveData<List<RoomListItem>>?

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }
}
