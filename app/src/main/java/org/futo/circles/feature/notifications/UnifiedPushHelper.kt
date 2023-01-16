package org.futo.circles.feature.notifications

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.futo.circles.R
import org.futo.circles.feature.notifications.model.DiscoveryResponse
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

    fun register(
        activity: AppCompatActivity,
        onDoneRunnable: Runnable? = null,
    ) {
        registerInternal(
            activity,
            onDoneRunnable = onDoneRunnable
        )
    }

    private fun registerInternal(
        activity: AppCompatActivity,
        onDoneRunnable: Runnable? = null
    ) {
        activity.lifecycleScope.launch {
            if (!vectorFeatures.allowExternalUnifiedPushDistributors()) {
                UnifiedPush.saveDistributor(context, context.packageName)
                UnifiedPush.registerApp(context)
                onDoneRunnable?.run()
                return@launch
            }

            if (UnifiedPush.getDistributor(context).isNotEmpty()) {
                UnifiedPush.registerApp(context)
                onDoneRunnable?.run()
                return@launch
            }

            val distributors = UnifiedPush.getDistributors(context)

            if (distributors.size == 1) {
                UnifiedPush.saveDistributor(context, distributors.first())
                UnifiedPush.registerApp(context)
                onDoneRunnable?.run()
            }
        }
    }


    suspend fun unregister(pushersManager: PushersManager? = null) {
        val mode = BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_REALTIME
        vectorPreferences.setFdroidSyncBackgroundMode(mode)
        try {
            getEndpointOrToken()?.let { pushersManager?.unregisterPusher(it) }
        } catch (ignore: Exception) {
        }

        preferencesProvider.storeUnifiedPushEndpoint(null)
        preferencesProvider.storePushGateway(null)
        UnifiedPush.unregisterApp(context)
    }


    suspend fun storeCustomOrDefaultGateway(
        endpoint: String,
        onDoneRunnable: Runnable? = null
    ) {
        if (UnifiedPush.getDistributor(context) == context.packageName) {
            preferencesProvider.storePushGateway(context.getString(R.string.pusher_http_url))
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

    fun getEndpointOrToken(): String? {
        return if (isEmbeddedDistributor()) fcmHelper.getFcmToken()
        else preferencesProvider.getUnifiedPushEndpoint()
    }

    fun getPushGateway(): String? {
        return if (isEmbeddedDistributor()) context.getString(R.string.pusher_http_url)
        else preferencesProvider.getPushGateway()
    }
}
