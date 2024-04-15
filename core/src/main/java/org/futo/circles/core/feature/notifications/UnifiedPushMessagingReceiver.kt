package org.futo.circles.core.feature.notifications

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.futo.circles.core.model.PushData
import org.futo.circles.core.model.PushDataUnifiedPush
import org.futo.circles.core.model.toPushData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.unifiedpush.android.connector.MessagingReceiver
import javax.inject.Inject

@AndroidEntryPoint
class UnifiedPushMessagingReceiver : MessagingReceiver() {

    @Inject
    lateinit var preferencesProvider: PreferencesProvider

    @Inject
    lateinit var pushHandler: PushHandler

    @Inject
    lateinit var pushersManager: PushersManager

    @Inject
    lateinit var guardServiceStarter: GuardServiceStarter
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