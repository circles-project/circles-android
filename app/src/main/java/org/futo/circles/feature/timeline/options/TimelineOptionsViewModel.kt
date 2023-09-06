package org.futo.circles.feature.timeline.options

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.leave.LeaveRoomDataSource
import org.futo.circles.feature.room.RoomNotificationsDataSource
import org.futo.circles.feature.timeline.data_source.AccessLevelDataSource
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class TimelineOptionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomNotificationsDataSource: RoomNotificationsDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource,
    accessLevelDataSource: AccessLevelDataSource,
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")


    val leaveDeleteEventLiveData = SingleEventLiveData<Response<Unit?>>()
    val accessLevelLiveData = accessLevelDataSource.accessLevelFlow.asLiveData()
    val notificationsStateLiveData = roomNotificationsDataSource.notificationsStateLiveData

    val roomSummaryLiveData =
        MatrixSessionProvider.getSessionOrThrow().getRoom(roomId)?.getRoomSummaryLive()

    fun delete(isGroup: Boolean) {
        launchBg {
            val result = if (isGroup) leaveRoomDataSource.deleteGroup()
            else leaveRoomDataSource.deleteCircle()
            leaveDeleteEventLiveData.postValue(result)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        launchBg { roomNotificationsDataSource.setNotificationsEnabled(enabled) }
    }

    fun leaveGroup() {
        launchBg {
            val result = leaveRoomDataSource.leaveGroup()
            leaveDeleteEventLiveData.postValue(result)
        }
    }

    fun canLeaveRoom(): Boolean = leaveRoomDataSource.canLeaveRoom()
}