package org.futo.circles.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.notifications.UnifiedPushHelper
import org.futo.circles.provider.MatrixSessionProvider
import org.unifiedpush.android.connector.UnifiedPush

class HomeViewModel(
    private val pushersManager: PushersManager,
    private val unifiedPushHelper: UnifiedPushHelper,
    private val fcmHelper: FcmHelper
) : ViewModel() {

    fun registerPushNotifications(context: Context) {
        UnifiedPush.saveDistributor(context, context.packageName)
        UnifiedPush.registerApp(context)
        if (unifiedPushHelper.isEmbeddedDistributor())
            fcmHelper.ensureFcmTokenIsRetrieved(pushersManager, shouldAddHttpPusher())
    }

    private fun shouldAddHttpPusher(): Boolean {
        val currentSession = MatrixSessionProvider.currentSession ?: return false
        val currentPushers = currentSession.pushersService().getPushers()
        return currentPushers.none { it.deviceId == currentSession.sessionParams.deviceId }
    }

}