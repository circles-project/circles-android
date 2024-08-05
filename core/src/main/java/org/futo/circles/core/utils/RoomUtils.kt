package org.futo.circles.core.utils

import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.Membership


fun getJoinedRoomById(roomId: String): Room? {
    val session = MatrixSessionProvider.currentSession ?: return null
    return session.roomService().getRoom(roomId)
        ?.takeIf { it.roomSummary()?.membership == Membership.JOIN }
}