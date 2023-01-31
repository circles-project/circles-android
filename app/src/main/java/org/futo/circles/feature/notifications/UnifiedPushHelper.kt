package org.futo.circles.feature.notifications

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.futo.circles.R
import org.futo.circles.core.PUSHER_URL
import org.futo.circles.model.DiscoveryResponse
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.PreferencesProvider
import org.matrix.android.sdk.api.cache.CacheStrategy
import org.unifiedpush.android.connector.UnifiedPush
import java.net.URL


class UnifiedPushHelper(
    private val context: Context,
    private val preferencesProvider: PreferencesProvider,
    private val fcmHelper: FcmHelper
) {


    suspend fun storeCustomOrDefaultGateway(
        endpoint: String,
        onDoneRunnable: Runnable? = null
    ) {
        if (UnifiedPush.getDistributor(context) == context.packageName) {
            preferencesProvider.storePushGateway(PUSHER_URL)
            onDoneRunnable?.run()
            return
        }

        val gateway = context.getString(R.string.default_push_gateway_http_url)
        val parsed = URL(endpoint)
        val custom = "${parsed.protocol}://${parsed.host}/_matrix/push/v1/notify"

        try {
            val response =
                MatrixInstanceProvider.matrix.rawService().getUrl(custom, CacheStrategy.NoCache)
            val discoveryResponse = Gson().fromJson(response, DiscoveryResponse::class.java)
            if (discoveryResponse.unifiedpush.gateway == "matrix") {
                preferencesProvider.storePushGateway(custom)
                onDoneRunnable?.run()
                return
            }
        } catch (ignore: Throwable) {
        }
        preferencesProvider.storePushGateway(gateway)
        onDoneRunnable?.run()
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

    fun getExternalDistributors(): List<String> {
        return UnifiedPush.getDistributors(context).filterNot { it == context.packageName }
    }

    fun getCurrentDistributorName(): String {
        return when {
            isEmbeddedDistributor() -> context.getString(R.string.unifiedpush_distributor_fcm_fallback)
            isBackgroundSync() -> context.getString(R.string.unifiedpush_distributor_background_sync)
            else -> UnifiedPush.getDistributor(context)
        }
    }

    fun getEndpointOrToken(): String? {
        return if (isEmbeddedDistributor()) fcmHelper.getFcmToken()
        else preferencesProvider.getUnifiedPushEndpoint()
    }

    fun getPushGateway(): String? {
        return if (isEmbeddedDistributor()) PUSHER_URL
        else preferencesProvider.getPushGateway()
    }
}
