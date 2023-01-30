package org.futo.circles.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.futo.circles.R
import org.futo.circles.feature.notifications.*
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.android.inject


class FirebaseMessagingService : FirebaseMessagingService() {

    private val fcmHelper: FcmHelper by inject()
    private val pushersManager: PushersManager by inject()
    private val vectorPushHandler: PushHandler by inject()
    private val unifiedPushHelper: UnifiedPushHelper by inject()

    override fun onNewToken(token: String) {
        fcmHelper.storeFcmToken(token)
        if (MatrixSessionProvider.currentSession != null && unifiedPushHelper.isEmbeddedDistributor()
        ) {
            pushersManager.enqueueRegisterPusher(token, getString(R.string.pusher_http_url))
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        PushParser.parsePushDataFcm(message.data).let {
            vectorPushHandler.handle(it)
        }
    }
}