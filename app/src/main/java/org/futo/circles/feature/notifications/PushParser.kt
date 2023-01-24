package org.futo.circles.feature.notifications

import com.google.gson.Gson
import org.futo.circles.model.PushData
import org.futo.circles.model.PushDataFcm
import org.futo.circles.model.PushDataUnifiedPush
import org.futo.circles.model.toPushData
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
