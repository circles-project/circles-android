package org.futo.circles.core.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.feature.notifications.NotificationUtils
import javax.inject.Inject

@AndroidEntryPoint
class GuardAndroidService : Service() {

    @Inject
    lateinit var notificationUtils: NotificationUtils

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
