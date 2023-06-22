package org.futo.circles.feature.room.well_known

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.model.RoomPublicInfo
import javax.inject.Inject

@HiltViewModel
class RoomWellKnownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataSource: RoomWellKnownDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val roomPublicInfoLiveData = SingleEventLiveData<Response<RoomPublicInfo>>()

    init {
        fetchRoomPublicInfo()
    }

    private fun fetchRoomPublicInfo() {
        launchBg {
            val result = dataSource.resolveRoomById(roomId)
            roomPublicInfoLiveData.postValue(result)
        }
    }

}