package com.futo.circles.feature.circles.following

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg

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