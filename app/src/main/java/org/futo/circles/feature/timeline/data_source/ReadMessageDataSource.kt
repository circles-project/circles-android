package org.futo.circles.feature.timeline.data_source

import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.read.ReadService
import javax.inject.Inject

class ReadMessageDataSource @Inject constructor() {

    suspend fun markRoomAsRead(roomId: String) {
        MatrixSessionProvider.currentSession?.roomService()?.getRoom(roomId)?.readService()
            ?.markAsRead(ReadService.MarkAsReadParams.BOTH, false)
    }
}