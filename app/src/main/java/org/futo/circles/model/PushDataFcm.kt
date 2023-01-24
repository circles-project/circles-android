package org.futo.circles.model

import org.matrix.android.sdk.api.MatrixPatterns

data class PushDataFcm(
    val eventId: String?,
    val roomId: String?,
    var unread: Int?,
)

fun PushDataFcm.toPushData() = PushData(
    eventId = eventId?.takeIf { MatrixPatterns.isEventId(it) },
    roomId = roomId?.takeIf { MatrixPatterns.isRoomId(it) },
    unread = unread
)
