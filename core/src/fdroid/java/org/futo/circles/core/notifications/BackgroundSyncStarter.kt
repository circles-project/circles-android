package org.futo.circles.core.notifications

import org.futo.circles.core.feature.notifications.CirclesSyncAndroidService.Companion.DEFAULT_SYNC_DELAY_SECONDS
import org.futo.circles.core.feature.notifications.CirclesSyncAndroidService.Companion.DEFAULT_SYNC_TIMEOUT_SECONDS
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import javax.inject.Inject

class BackgroundSyncStarter @Inject constructor(
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
