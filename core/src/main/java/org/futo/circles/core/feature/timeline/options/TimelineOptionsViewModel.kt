package org.futo.circles.core.feature.timeline.options

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.RoomNotificationsDataSource
import org.futo.circles.core.feature.room.leave.LeaveRoomDataSource
import org.futo.circles.core.feature.room.requests.KnockRequestsDataSource
import org.futo.circles.core.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class TimelineOptionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomNotificationsDataSource: RoomNotificationsDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource,
    knockRequestsDataSource: KnockRequestsDataSource,
    accessLevelDataSource: AccessLevelDataSource,
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    val leaveDeleteEventLiveData = SingleEventLiveData<Response<Unit?>>()
    val accessLevelLiveData = accessLevelDataSource.accessLevelFlow.asLiveData()
    val notificationsStateLiveData = roomNotificationsDataSource.notificationsStateLiveData
    val knockRequestCountLiveData =
        knockRequestsDataSource.getKnockRequestCountFlow(roomId).asLiveData()

    val roomSummaryLiveData =
        MatrixSessionProvider.getSessionOrThrow().getRoom(roomId)?.getRoomSummaryLive()

    fun delete() {
        launchBg {
            val result = leaveRoomDataSource.deleteRoom()
            leaveDeleteEventLiveData.postValue(result)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        launchBg { roomNotificationsDataSource.setNotificationsEnabled(enabled) }
    }

    fun leaveRoom() {
        launchBg {
            val result = leaveRoomDataSource.leaveGroup()
            leaveDeleteEventLiveData.postValue(result)
        }
    }

    fun canLeaveRoom(): Boolean = leaveRoomDataSource.canLeaveRoom()

}
