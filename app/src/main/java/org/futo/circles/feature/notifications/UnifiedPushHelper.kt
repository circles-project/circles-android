package org.futo.circles.feature.notifications

import android.content.Context
import com.google.gson.Gson
import org.futo.circles.R
import org.futo.circles.core.DEFAULT_PUSH_GATEWAY
import org.futo.circles.core.getPusherUrl
import org.futo.circles.extensions.getApplicationLabel
import org.futo.circles.model.DiscoveryResponse
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.PreferencesProvider
import org.matrix.android.sdk.api.cache.CacheStrategy
import org.unifiedpush.android.connector.UnifiedPush
import java.net.URL

class UnifiedPushHelper(
    private val context: Context,
    private val fcmHelper: FcmHelper,
    private val preferencesProvider: PreferencesProvider
) {

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
}
