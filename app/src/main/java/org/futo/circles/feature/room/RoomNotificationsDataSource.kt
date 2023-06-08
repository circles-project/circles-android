package org.futo.circles.feature.room

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.assisted.AssistedFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import javax.inject.Inject

@ViewModelScoped
class RoomNotificationsDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val type: CircleRoomTypeArg = savedStateHandle.getOrThrow("type")

    private val session
        get() = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )

    private val timelineId by lazy {
        if (type == CircleRoomTypeArg.Group) roomId
        else getTimelineRoomFor(roomId)?.roomId ?: throw IllegalArgumentException(
            context.getString(R.string.timeline_not_found)
        )
    }

    val notificationsStateLiveData =
        session.getRoom(timelineId)?.roomPushRuleService()?.getLiveRoomNotificationState()?.map {
            it != RoomNotificationState.MUTE
        } ?: MutableLiveData(true)

    suspend fun setNotificationsEnabled(enabled: Boolean) = createResult {
        getTimelineRooms().forEach {
            it.roomPushRuleService().setRoomNotificationState(
                if (enabled) RoomNotificationState.ALL_MESSAGES_NOISY else RoomNotificationState.MUTE
            )
        }
    }

    private fun getTimelineRooms(): List<Room> = when (type) {
        CircleRoomTypeArg.Circle -> session.getRoom(roomId)
            ?.roomSummary()?.spaceChildren?.mapNotNull {
                session.getRoom(it.childRoomId)
            } ?: emptyList()

        else -> listOfNotNull(session.getRoom(roomId))
    }
}