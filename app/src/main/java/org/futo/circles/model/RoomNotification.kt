package org.futo.circles.model

import android.app.Notification

sealed interface RoomNotification {
    data class Removed(val roomId: String) : RoomNotification
    data class Message(val notification: Notification, val meta: Meta) : RoomNotification {
        data class Meta(
            val summaryLine: CharSequence,
            val messageCount: Int,
            val latestTimestamp: Long,
            val roomId: String,
            val shouldBing: Boolean
        )
    }
}

sealed interface OneShotNotification {
    data class Removed(val key: String) : OneShotNotification
    data class Append(val notification: Notification, val meta: Meta) : OneShotNotification {
        data class Meta(
            val key: String,
            val summaryLine: CharSequence,
            val isNoisy: Boolean,
            val timestamp: Long,
        )
    }
}

sealed interface SummaryNotification {
    object Removed : SummaryNotification
    data class Update(val notification: Notification) : SummaryNotification
}