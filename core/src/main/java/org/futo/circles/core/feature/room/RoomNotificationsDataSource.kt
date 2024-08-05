package org.futo.circles.core.feature.room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.notification.RoomNotificationState
import javax.inject.Inject

@ViewModelScoped
class RoomNotificationsDataSource @Inject constructor() {

    private val session
        get() = MatrixSessionProvider.getSessionOrThrow()

    fun getNotificationsStateLiveData(roomId: String) =
        session.getRoom(roomId)?.roomPushRuleService()?.getLiveRoomNotificationState()?.map {
            it != RoomNotificationState.MUTE
        } ?: MutableLiveData(true)

    suspend fun setNotificationsEnabled(roomId: String, enabled: Boolean) = createResult {
        session.getRoom(roomId)?.roomPushRuleService()?.setRoomNotificationState(
            if (enabled) RoomNotificationState.ALL_MESSAGES_NOISY else RoomNotificationState.MUTE
        )
    }
}