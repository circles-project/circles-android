package org.futo.circles.notifications

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.matrix.android.sdk.api.extensions.tryOrNull

class FdroidGuardServiceStarter(
    private val context: Context
) : GuardServiceStarter {

    override fun start() {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled())
            tryOrNull {
                val intent = Intent(context, GuardAndroidService::class.java)
                ContextCompat.startForegroundService(context, intent)
            }
    }

    override fun stop() {
        val intent = Intent(context, GuardAndroidService::class.java)
        context.stopService(intent)
    }
}
