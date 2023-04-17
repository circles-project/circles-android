package org.futo.circles.feature.notifications

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.futo.circles.model.PushData
import org.futo.circles.model.PushDataUnifiedPush
import org.futo.circles.model.toPushData
import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.provider.PreferencesProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.unifiedpush.android.connector.MessagingReceiver


class UnifiedPushMessagingReceiver : MessagingReceiver(), KoinComponent {

    private val preferencesProvider: PreferencesProvider by inject()
    private val pushHandler: PushHandler by inject()
    private val pushersManager: PushersManager by inject()
    private val guardServiceStarter: GuardServiceStarter by inject()
    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onMessage(context: Context, message: ByteArray, instance: String) {
        parsePushDataUnifiedPush(message)?.let { pushHandler.handle(it) }
    }

    override fun onNewEndpoint(context: Context, endpoint: String, instance: String) {
        MatrixSessionProvider.currentSession?.let {
            if (pushersManager.getEndpointOrToken() != endpoint) {
                preferencesProvider.storeUpEndpoint(endpoint)
                coroutineScope.launch {
                    pushersManager.storeCustomOrDefaultGateway(endpoint) {
                        pushersManager.getPushGateway()?.let {
                            pushersManager.enqueueRegisterPusher(endpoint, it)
                        }
                    }
                }
            }
        }
        preferencesProvider.setFdroidBackgroundSyncEnabled(false)
        guardServiceStarter.stop()
    }

    override fun onRegistrationFailed(context: Context, instance: String) {
        preferencesProvider.setFdroidBackgroundSyncEnabled(true)
        guardServiceStarter.start()
    }

    override fun onUnregistered(context: Context, instance: String) {
        preferencesProvider.setFdroidBackgroundSyncEnabled(true)
        guardServiceStarter.start()
        runBlocking {
            try {
                pushersManager.unregisterPusher(pushersManager.getEndpointOrToken().orEmpty())
            } catch (_: Exception) {
            }
        }
    }

    private fun parsePushDataUnifiedPush(message: ByteArray): PushData? =
        tryOrNull {
            Gson().fromJson(String(message), PushDataUnifiedPush::class.java)?.toPushData()
        }

}