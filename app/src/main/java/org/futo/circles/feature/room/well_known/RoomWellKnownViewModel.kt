package org.futo.circles.feature.room.well_known

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.RoomUrlData
import org.futo.circles.model.UrlData
import org.futo.circles.model.UserPublicInfo
import org.futo.circles.model.UserUrlData
import org.futo.circles.model.parseUrlData
import javax.inject.Inject

@HiltViewModel
class RoomWellKnownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: RoomWellKnownDataSource
) : ViewModel() {

    private val url: String = savedStateHandle.getOrThrow("url")

    val roomPublicInfoLiveData = SingleEventLiveData<Response<RoomPublicInfo>>()
    val userPublicInfoLiveData = SingleEventLiveData<Response<UserPublicInfo>>()
    val knockRequestLiveData = SingleEventLiveData<Response<Unit?>>()
    val parseErrorEventLiveData = SingleEventLiveData<Unit>()
    private var urlData: UrlData? = parseUrlData(url)

    init {
        when (val data = urlData) {
            is RoomUrlData -> fetchRoomPublicInfo(data)
            is UserUrlData -> fetchUserPublicInfo(data)
            null -> parseErrorEventLiveData.postValue(Unit)
        }
    }

    private fun fetchRoomPublicInfo(data: RoomUrlData) {
        launchBg {
            val result = dataSource.resolveRoom(data)
            roomPublicInfoLiveData.postValue(result)
        }
    }

    private fun fetchUserPublicInfo(data: UserUrlData) {
        launchBg {
            val result = dataSource.resolveUser(data)
            userPublicInfoLiveData.postValue(result)
        }
    }

    fun sendKnockRequest() {
        val roomId = urlData?.knockRoomId ?: return
        launchBg {
            val result =
                createResult { MatrixSessionProvider.currentSession?.roomService()?.knock(roomId) }
            knockRequestLiveData.postValue(result)
        }
    }

}