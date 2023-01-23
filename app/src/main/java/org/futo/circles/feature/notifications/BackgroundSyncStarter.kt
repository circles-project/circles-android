package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.provider.PreferencesProvider

class BackgroundSyncStarter(
    private val context: Context,
    private val preferencesProvider: PreferencesProvider
) {
    fun start() {
        if (preferencesProvider.areNotificationEnabledForDevice()) {
            val activeSession = MatrixSessionProvider.currentSession ?: return
            when (preferencesProvider.getFdroidSyncBackgroundMode()) {
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY -> {
                    activeSession.syncService().startAutomaticBackgroundSync(
                        DEFAULT_SYNC_TIMEOUT_SECONDS.toLong(),
                        DEFAULT_SYNC_DELAY_SECONDS.toLong()
                    )
                }
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_REALTIME -> {
                    AlarmSyncBroadcastReceiver.scheduleAlarm(
                        context,
                        activeSession.sessionId,
                        DEFAULT_SYNC_DELAY_SECONDS
                    )
                }
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_DISABLED -> {}
            }
        }
    }

    companion object {
        const val DEFAULT_SYNC_DELAY_SECONDS = 60
        const val DEFAULT_SYNC_TIMEOUT_SECONDS = 6
    }
}
