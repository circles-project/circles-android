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
import org.futo.circles.model.parseUrlData
import javax.inject.Inject

@HiltViewModel
class RoomWellKnownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: RoomWellKnownDataSource
) : ViewModel() {

    private val url: String = savedStateHandle.getOrThrow("url")

    val roomPublicInfoLiveData = SingleEventLiveData<Response<RoomPublicInfo>>()
    val knockRequestLiveData = SingleEventLiveData<Response<Unit?>>()
    val parseErrorEventLiveData = SingleEventLiveData<Unit>()
    private var urlData: RoomUrlData? = parseUrlData(url)

    init {
        urlData?.let { fetchRoomPublicInfo(it) } ?: parseErrorEventLiveData.postValue(Unit)
    }

    private fun fetchRoomPublicInfo(data: RoomUrlData) {
        launchBg {
            val result = dataSource.resolveRoom(data)
            roomPublicInfoLiveData.postValue(result)
        }
    }

    fun sendKnockRequest(message: String?) {
        val roomId = urlData?.roomId ?: return
        launchBg {
            val result =
                createResult {
                    MatrixSessionProvider.currentSession?.roomService()?.knock(roomId, message)
                }
            knockRequestLiveData.postValue(result)
        }
    }

}