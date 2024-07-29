package org.futo.circles.core.feature.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import javax.inject.Inject

@ViewModelScoped
class RoomNotificationsDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    private val session
        get() = MatrixSessionProvider.getSessionOrThrow()

    val notificationsStateLiveData =
        session.getRoom(roomId)?.roomPushRuleService()?.getLiveRoomNotificationState()?.map {
            it != RoomNotificationState.MUTE
        } ?: MutableLiveData(true)

    suspend fun setNotificationsEnabled(enabled: Boolean) = createResult {
        session.getRoom(roomId)?.roomPushRuleService()?.setRoomNotificationState(
            if (enabled) RoomNotificationState.ALL_MESSAGES_NOISY else RoomNotificationState.MUTE
        )
    }
}