package org.futo.circles.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.PushHandler
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.model.PushData
import org.koin.android.ext.android.inject
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.extensions.tryOrNull


class FirebaseMessagingService : FirebaseMessagingService() {

    private val fcmHelper: FcmHelper by inject()
    private val pushersManager: PushersManager by inject()
    private val pushHandler: PushHandler by inject()

    override fun onNewToken(token: String) {
        fcmHelper.storeFcmToken(token)
        pushersManager.enqueueRegisterPusherWithFcmKey(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        pushHandler.handle(message.toPushData())
    }

    private fun RemoteMessage.toPushData(): PushData = PushData(
        eventId = data["event_id"].takeIf { MatrixPatterns.isEventId(it) },
        roomId = data["room_id"]?.takeIf { MatrixPatterns.isRoomId(it) },
        unread = data["unread"]?.let { tryOrNull { Integer.parseInt(it) } }
    )
}