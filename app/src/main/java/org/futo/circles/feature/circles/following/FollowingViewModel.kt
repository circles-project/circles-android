package org.futo.circles.feature.circles.following

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class FollowingViewModel(
    private val dataSource: FollowingDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.roomsLiveData

    val removeResponseLiveData = SingleEventLiveData<Response<Unit>>()

    fun removeRoomFromCircle(roomId: String) {
        launchBg {
            val response = dataSource.removeRoomRelations(roomId)
            removeResponseLiveData.postValue(response)
        }
    }

    fun unfollowRoom(roomId: String) {
        launchBg {
            val response = dataSource.unfollowRoom(roomId)
            removeResponseLiveData.postValue(response)
        }
    }
}