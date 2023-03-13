package org.futo.circles.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.futo.circles.R
import org.futo.circles.feature.notifications.NotificationUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class GuardAndroidService : Service(), KoinComponent {

    private val notificationUtils: NotificationUtils by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationSubtitleRes = R.string.notification_listening_for_notifications
        val notification =
            notificationUtils.buildForegroundServiceNotification(notificationSubtitleRes, false)
        startForeground(NotificationUtils.NOTIFICATION_ID_FOREGROUND_SERVICE, notification)
        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
