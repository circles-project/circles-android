package org.futo.circles.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import org.futo.circles.feature.notifications.CirclesSyncAndroidService.Companion.DEFAULT_SYNC_DELAY_SECONDS
import org.futo.circles.feature.notifications.CirclesSyncAndroidService.Companion.DEFAULT_SYNC_TIMEOUT_SECONDS
import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.provider.PreferencesProvider

class BackgroundSyncStarter(
    private val context: Context
) {
    fun start() {
        val activeSession = MatrixSessionProvider.currentSession ?: return
        if (NotificationManagerCompat.from(context).areNotificationsEnabled())
            activeSession.syncService().startAutomaticBackgroundSync(
                DEFAULT_SYNC_TIMEOUT_SECONDS.toLong(),
                DEFAULT_SYNC_DELAY_SECONDS.toLong()
            )
    }
}
