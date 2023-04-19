package org.futo.circles.notifications

import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.provider.MatrixSessionProvider

class FdroidFcmHelper(
    private val backgroundSyncStarter: BackgroundSyncStarter
) : FcmHelper {

    override fun isFirebaseAvailable(): Boolean = false

    override fun getFcmToken(): String? {
        return null
    }

    override fun storeFcmToken(token: String?) {}

    override fun ensureFcmTokenIsRetrieved(
        pushersManager: PushersManager,
        registerPusher: Boolean
    ) {
    }

    override fun onEnterForeground() {
        MatrixSessionProvider.currentSession?.syncService()?.stopAnyBackgroundSync()
    }

    override fun onEnterBackground() {
        backgroundSyncStarter.start()
    }
}
