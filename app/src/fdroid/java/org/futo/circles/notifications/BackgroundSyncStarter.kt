package org.futo.circles.notifications

import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.feature.notifications.CirclesSyncAndroidService.Companion.DEFAULT_SYNC_DELAY_SECONDS
import org.futo.circles.feature.notifications.CirclesSyncAndroidService.Companion.DEFAULT_SYNC_TIMEOUT_SECONDS

class BackgroundSyncStarter(
    private val preferencesProvider: PreferencesProvider
) {
    fun start() {
        val activeSession = MatrixSessionProvider.currentSession ?: return
        if (preferencesProvider.isFdroidBackgroundSyncEnabled())
            activeSession.syncService().startAutomaticBackgroundSync(
                DEFAULT_SYNC_TIMEOUT_SECONDS.toLong(),
                DEFAULT_SYNC_DELAY_SECONDS.toLong()
            )
    }
}
