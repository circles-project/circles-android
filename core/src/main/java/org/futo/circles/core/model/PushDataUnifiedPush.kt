package org.futo.circles.core.model

import com.google.gson.annotations.SerializedName
import org.matrix.android.sdk.api.MatrixPatterns

data class PushDataUnifiedPush(
    @SerializedName("notification") val notification: PushDataUnifiedPushNotification?
)

data class PushDataUnifiedPushNotification(
    @SerializedName("event_id") val eventId: String?,
    @SerializedName("room_id") val roomId: String?,
    @SerializedName("counts") var counts: PushDataUnifiedPushCounts?,
)

data class PushDataUnifiedPushCounts(
    @SerializedName("unread") val unread: Int?
)

fun PushDataUnifiedPush.toPushData() = PushData(
    eventId = notification?.eventId?.takeIf { MatrixPatterns.isEventId(it) },
    roomId = notification?.roomId?.takeIf { MatrixPatterns.isRoomId(it) },
    unread = notification?.counts?.unread
)
