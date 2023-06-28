package org.futo.circles.feature.room.well_known

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.UserPublicInfo
import javax.inject.Inject

@HiltViewModel
class RoomWellKnownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: RoomWellKnownDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val userId: String? = savedStateHandle["userId"]

    val roomPublicInfoLiveData = SingleEventLiveData<Response<RoomPublicInfo>>()
    val userPublicInfoLiveData = SingleEventLiveData<Response<UserPublicInfo>>()
    val knockRequestLiveData = SingleEventLiveData<Response<Unit?>>()

    init {
        userId?.let { fetchUserPublicInfo(it) } ?: kotlin.run { fetchRoomPublicInfo() }
    }

    private fun fetchRoomPublicInfo() {
        launchBg {
            val result = dataSource.resolveRoomById(roomId)
            roomPublicInfoLiveData.postValue(result)
        }
    }

    private fun fetchUserPublicInfo(userId: String) {
        launchBg {
            val result = dataSource.resolveUserById(userId, roomId)
            userPublicInfoLiveData.postValue(result)
        }
    }

    fun sendKnockRequest() {
        launchBg {
            val result =
                createResult { MatrixSessionProvider.currentSession?.roomService()?.knock(roomId) }
            knockRequestLiveData.postValue(result)
        }
    }

}