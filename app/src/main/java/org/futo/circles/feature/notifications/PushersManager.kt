package org.futo.circles.feature.notifications

import android.content.Context
import com.google.gson.Gson
import org.futo.circles.R
import org.futo.circles.base.DEFAULT_PUSH_GATEWAY
import org.futo.circles.base.PUSHER_APP_ID
import org.futo.circles.base.getPusherUrl
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.extensions.getApplicationLabel
import org.futo.circles.model.DiscoveryResponse
import org.matrix.android.sdk.api.cache.CacheStrategy
import org.matrix.android.sdk.api.session.pushers.HttpPusher
import org.unifiedpush.android.connector.UnifiedPush
import java.net.URL
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

class PushersManager(
    private val context: Context,
    private val fcmHelper: FcmHelper,
    private val preferencesProvider: PreferencesProvider,
    private val guardServiceStarter: GuardServiceStarter
) {

    suspend fun testPush() {
        MatrixSessionProvider.currentSession?.pushersService()?.testPush(
            getPushGateway() ?: return,
            PUSHER_APP_ID,
            getEndpointOrToken().orEmpty(),
            TEST_EVENT_ID
        )
    }

    fun enqueueRegisterPusherWithFcmKey(pushKey: String): UUID {
        return enqueueRegisterPusher(pushKey, getPusherUrl())
    }

    fun enqueueRegisterPusher(
        pushKey: String,
        gateway: String
    ): UUID {
        val currentSession =
            MatrixSessionProvider.currentSession
                ?: throw IllegalArgumentException(context.getString(R.string.session_is_not_created))
        val pusher = createHttpPusher(pushKey, gateway)
        return currentSession.pushersService().enqueueAddHttpPusher(pusher)
    }

    fun registerPushNotifications(distributor: String? = null) {
        registerUnifiedPush(distributor)
        if (isEmbeddedDistributor())
            fcmHelper.ensureFcmTokenIsRetrieved(this, shouldAddHttpPusher())
        if (isBackgroundSync()) guardServiceStarter.start()
    }

    suspend fun unregisterPusher(pushKey: String) {
        MatrixSessionProvider.currentSession?.pushersService()
            ?.removeHttpPusher(pushKey, PUSHER_APP_ID)
    }

    private fun registerUnifiedPush(distributor: String? = null) {
        distributor?.let {
            saveAndRegisterApp(distributor)
            return
        }
        if (UnifiedPush.getDistributor(context).isNotEmpty()) {
            UnifiedPush.registerApp(context)
            return
        }
        val distributors = getAllDistributors()
        return if (distributors.size == 1) saveAndRegisterApp(distributors.first())
        else saveAndRegisterApp(context.packageName)
    }

    private fun shouldAddHttpPusher(): Boolean {
        val currentSession = MatrixSessionProvider.currentSession ?: return false
        val currentPushers = currentSession.pushersService().getPushers()
        return currentPushers.none { it.deviceId == currentSession.sessionParams.deviceId }
    }

    private fun createHttpPusher(
        pushKey: String,
        gateway: String
    ) = HttpPusher(
        pushKey,
        PUSHER_APP_ID,
        profileTag = DEFAULT_PUSHER_FILE_TAG + "_" + abs(MatrixSessionProvider.currentSession?.myUserId.hashCode()),
        Locale.getDefault().language,
        context.getString(R.string.app_name),
        MatrixSessionProvider.currentSession?.sessionParams?.deviceId ?: DEFAULT_PUSHER_FILE_TAG,
        gateway,
        enabled = true,
        deviceId = MatrixSessionProvider.currentSession?.sessionParams?.deviceId
            ?: DEFAULT_PUSHER_FILE_TAG,
        append = false,
        withEventIdOnly = true
    )


    private fun saveAndRegisterApp(distributor: String) {
        UnifiedPush.saveDistributor(context, distributor)
        UnifiedPush.registerApp(context)
    }

    suspend fun unregisterUnifiedPush() {
        preferencesProvider.setFdroidBackgroundSyncEnabled(true)
        try {
            getEndpointOrToken()?.let { unregisterPusher(it) }
        } catch (_: Exception) {
        }
        preferencesProvider.storeUpEndpoint(null)
        preferencesProvider.storePushGateway(null)
        UnifiedPush.unregisterApp(context)
    }

    suspend fun storeCustomOrDefaultGateway(
        endpoint: String,
        onDoneRunnable: Runnable? = null
    ) {
        if (UnifiedPush.getDistributor(context) == context.packageName) {
            preferencesProvider.storePushGateway(getPusherUrl())
            onDoneRunnable?.run()
            return
        }
        val gateway = DEFAULT_PUSH_GATEWAY
        val parsed = URL(endpoint)
        val custom = "${parsed.protocol}://${parsed.host}/_matrix/push/v1/notify"
        try {
            val response =
                MatrixInstanceProvider.matrix.rawService().getUrl(custom, CacheStrategy.NoCache)
            Gson().fromJson(response, DiscoveryResponse::class.java)?.let { discoveryResponse ->
                if (discoveryResponse.unifiedpush.gateway == "matrix") {
                    preferencesProvider.storePushGateway(custom)
                    onDoneRunnable?.run()
                    return
                }
            }
        } catch (_: Throwable) {
        }
        preferencesProvider.storePushGateway(gateway)
        onDoneRunnable?.run()
    }

    fun getCurrentDistributor() = UnifiedPush.getDistributor(context)

    fun getAllDistributors() = UnifiedPush.getDistributors(context)

    fun getAvailableDistributorsNames(): List<String> {
        val internalDistributorName = context.getString(
            if (fcmHelper.isFirebaseAvailable()) R.string.unifiedpush_distributor_fcm_fallback
            else R.string.unifiedpush_distributor_background_sync
        )
        val distributors = UnifiedPush.getDistributors(context)
        return distributors.map {
            if (it == context.packageName) internalDistributorName
            else context.getApplicationLabel(it)
        }
    }

    fun getExternalDistributors(): List<String> {
        return UnifiedPush.getDistributors(context)
            .filterNot { it == context.packageName }
    }

    fun getCurrentDistributorName(): String {
        return when {
            isEmbeddedDistributor() -> context.getString(R.string.unifiedpush_distributor_fcm_fallback)
            isBackgroundSync() -> context.getString(R.string.unifiedpush_distributor_background_sync)
            else -> context.getApplicationLabel(UnifiedPush.getDistributor(context))
        }
    }

    fun isEmbeddedDistributor(): Boolean {
        return isInternalDistributor() && fcmHelper.isFirebaseAvailable()
    }

    fun isBackgroundSync(): Boolean {
        return isInternalDistributor() && !fcmHelper.isFirebaseAvailable()
    }

    private fun isInternalDistributor(): Boolean {
        return UnifiedPush.getDistributor(context).isEmpty() ||
                UnifiedPush.getDistributor(context) == context.packageName
    }

    fun getPrivacyFriendlyUpEndpoint(): String? {
        val endpoint = getEndpointOrToken()
        if (endpoint.isNullOrEmpty()) return null
        if (isEmbeddedDistributor()) {
            return endpoint
        }
        return try {
            val parsed = URL(endpoint)
            "${parsed.protocol}://${parsed.host}/***"
        } catch (_: Exception) {
            null
        }
    }

    fun getEndpointOrToken(): String? {
        return if (isEmbeddedDistributor()) fcmHelper.getFcmToken()
        else preferencesProvider.getEndpoint()
    }

    fun getPushGateway(): String? {
        return if (isEmbeddedDistributor()) getPusherUrl()
        else preferencesProvider.getPushGateway()
    }

    companion object {
        const val TEST_EVENT_ID = "\$THIS_IS_A_FAKE_EVENT_ID"
        private const val DEFAULT_PUSHER_FILE_TAG = "mobile"
    }
}
