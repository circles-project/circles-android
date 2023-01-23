package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.provider.MatrixSessionProvider

class FdroidFcmHelper(
    private val context: Context,
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
        // No op
    }

    override fun onEnterForeground() {
        MatrixSessionProvider.currentSession?.syncService()?.stopAnyBackgroundSync()
        AlarmSyncBroadcastReceiver.cancelAlarm(context)
    }

    override fun onEnterBackground() {
        backgroundSyncStarter.start()
    }
}
