package org.futo.circles.core.rooms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.rooms.data_source.RoomsDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.RoomListItem


abstract class RoomsViewModel(
    private val dataSource: RoomsDataSource
) : ViewModel() {

    abstract val roomsLiveData: LiveData<List<RoomListItem>>?

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }
}
