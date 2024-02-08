package org.futo.circles.core.feature.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import javax.inject.Inject

@ViewModelScoped
class RoomNotificationsDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val timelineId: String? = savedStateHandle["timelineId"]
    private val isCircle: Boolean = timelineId != null

    private val session
        get() = MatrixSessionProvider.getSessionOrThrow()

    val notificationsStateLiveData =
        session.getRoom(timelineId?:roomId)?.roomPushRuleService()?.getLiveRoomNotificationState()?.map {
            it != RoomNotificationState.MUTE
        } ?: MutableLiveData(true)

    suspend fun setNotificationsEnabled(enabled: Boolean) = createResult {
        getTimelineRooms().forEach {
            it.roomPushRuleService().setRoomNotificationState(
                if (enabled) RoomNotificationState.ALL_MESSAGES_NOISY else RoomNotificationState.MUTE
            )
        }
    }

    private fun getTimelineRooms(): List<Room> = if (isCircle) {
        session.getRoom(roomId)
            ?.roomSummary()?.spaceChildren?.mapNotNull {
                session.getRoom(it.childRoomId)
            } ?: emptyList()
    } else {
        listOfNotNull(session.getRoom(roomId))
    }

}