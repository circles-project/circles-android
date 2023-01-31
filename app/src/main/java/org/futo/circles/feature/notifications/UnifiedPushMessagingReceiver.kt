package org.futo.circles.feature.notifications

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.provider.PreferencesProvider
import org.unifiedpush.android.connector.MessagingReceiver


class UnifiedPushMessagingReceiver(
    private val pushersManager: PushersManager,
    private val preferencesProvider: PreferencesProvider,
    private val pushHandler: PushHandler,
    private val guardServiceStarter: GuardServiceStarter,
    private val unifiedPushHelper: UnifiedPushHelper
) : MessagingReceiver() {

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onMessage(context: Context, message: ByteArray, instance: String) {
        PushParser.parsePushDataUnifiedPush(message)?.let { pushHandler.handle(it) }
    }

    override fun onNewEndpoint(context: Context, endpoint: String, instance: String) {
        if (MatrixSessionProvider.currentSession != null) {
            if (unifiedPushHelper.getEndpointOrToken() != endpoint) {
                preferencesProvider.storeUnifiedPushEndpoint(endpoint)
                coroutineScope.launch {
                    unifiedPushHelper.storeCustomOrDefaultGateway(endpoint) {
                        unifiedPushHelper.getPushGateway()?.let {
                            pushersManager.enqueueRegisterPusher(endpoint, it)
                        }
                    }
                }
            }
        }
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
        runBlocking {
            try {
                pushersManager.unregisterPusher(unifiedPushHelper.getEndpointOrToken().orEmpty())
            } catch (ignore: Exception) {
            }
        }
    }
}