package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.provider.PreferencesProvider
import org.unifiedpush.android.connector.MessagingReceiver


class UnifiedPushMessagingReceiver(
    private val preferencesProvider: PreferencesProvider,
    private val pushHandler: PushHandler,
    private val guardServiceStarter: GuardServiceStarter
) : MessagingReceiver() {

    override fun onMessage(context: Context, message: ByteArray, instance: String) {
        PushParser.parsePushDataUnifiedPush(message)?.let { pushHandler.handle(it) }
    }

    override fun onNewEndpoint(context: Context, endpoint: String, instance: String) {
        preferencesProvider.setFdroidSyncBackgroundMode(BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_DISABLED)
        guardServiceStarter.stop()
    }

    override fun onRegistrationFailed(context: Context, instance: String) {
        preferencesProvider.setFdroidSyncBackgroundMode(BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_REALTIME)
        guardServiceStarter.start()
    }

    override fun onUnregistered(context: Context, instance: String) {
        preferencesProvider.setFdroidSyncBackgroundMode(BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_REALTIME)
        guardServiceStarter.start()
    }
}