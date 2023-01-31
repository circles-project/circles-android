package org.futo.circles.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.futo.circles.feature.notifications.*
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.android.inject


class FirebaseMessagingService : FirebaseMessagingService() {

    private val fcmHelper: FcmHelper by inject()
    private val pushersManager: PushersManager by inject()
    private val vectorPushHandler: PushHandler by inject()

    override fun onNewToken(token: String) {
        fcmHelper.storeFcmToken(token)
        MatrixSessionProvider.currentSession?.let {
            pushersManager.enqueueRegisterPusherWithFcmKey(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("MyLog", message.toString())
        PushParser.parsePushDataFcm(message.data).let {
            vectorPushHandler.handle(it)
        }
    }
}