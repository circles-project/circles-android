package org.futo.circles.core.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.feature.notifications.FcmHelper
import org.futo.circles.core.feature.notifications.PushHandler
import org.futo.circles.core.feature.notifications.PushersManager
import org.futo.circles.core.model.PushData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.extensions.tryOrNull
import javax.inject.Inject


@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmHelper: FcmHelper

    @Inject
    lateinit var pushersManager: PushersManager

    @Inject
    lateinit var pushHandler: PushHandler

    override fun onNewToken(token: String) {
        MatrixSessionProvider.currentSession ?: return
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