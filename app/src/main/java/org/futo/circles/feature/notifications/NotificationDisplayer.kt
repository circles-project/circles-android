package org.futo.circles.feature.notifications

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat

class NotificationDisplayer(context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    fun showNotificationMessage(tag: String?, id: Int, notification: Notification) {
        notificationManager.notify(tag, id, notification)
    }

    fun cancelNotificationMessage(tag: String?, id: Int) {
        notificationManager.cancel(tag, id)
    }

    fun cancelAllNotifications() {
        try {
            notificationManager.cancelAll()
        } catch (ignore: Exception) {
        }
    }
}
