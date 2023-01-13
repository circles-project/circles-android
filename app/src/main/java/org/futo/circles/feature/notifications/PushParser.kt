package org.futo.circles.feature.notifications

import com.google.gson.Gson
import org.futo.circles.feature.notifications.model.PushData
import org.futo.circles.feature.notifications.model.PushDataFcm
import org.futo.circles.feature.notifications.model.PushDataUnifiedPush
import org.futo.circles.feature.notifications.model.toPushData
import org.matrix.android.sdk.api.extensions.tryOrNull


class PushParser {
    
    fun parsePushDataUnifiedPush(message: ByteArray): PushData? {
        return try {
            Gson().fromJson(String(message), PushDataUnifiedPush::class.java).toPushData()
        } catch (ignore: Exception) {
            null
        }
    }

    fun parsePushDataFcm(message: Map<String, String?>): PushData {
        val pushDataFcm = PushDataFcm(
            eventId = message["event_id"],
            roomId = message["room_id"],
            unread = message["unread"]?.let { tryOrNull { Integer.parseInt(it) } },
        )
        return pushDataFcm.toPushData()
    }
}
