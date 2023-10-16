package org.futo.circles.core.feature.room.circles.following

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
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