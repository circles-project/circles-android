package org.futo.circles.provider

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import org.futo.circles.feature.notifications.BackgroundSyncMode

class PreferencesProvider(
    private val context: Context
) {

    private fun getSharedPreferences() =
        context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

    fun setDeveloperMode(isEnabled: Boolean) {
        getSharedPreferences().edit { putBoolean(DEV_MODE_KEY, isEnabled) }
    }

    fun isDeveloperModeEnabled(): Boolean = getSharedPreferences().getBoolean(DEV_MODE_KEY, false)

    fun getUnifiedPushEndpoint(): String? =
        getSharedPreferences().getString(PUSH_ENDPOINT_OR_TOKEN, null)

    fun storeUnifiedPushEndpoint(endpoint: String?) {
        getSharedPreferences().edit { putString(PUSH_ENDPOINT_OR_TOKEN, endpoint) }
    }

    fun getPushGateway(): String? = getSharedPreferences().getString(PUSH_GATEWAY, null)

    fun storePushGateway(gateway: String?) {
        getSharedPreferences().edit { putString(PUSH_GATEWAY, gateway) }
    }

    fun setFdroidSyncBackgroundMode(mode: BackgroundSyncMode) {
        getSharedPreferences().edit {
            putString(FDROID_BACKGROUND_SYNC_MODE, mode.name)
        }
    }

    fun getFdroidSyncBackgroundMode(): BackgroundSyncMode {
        val strPref = getSharedPreferences()
            .getString(
                FDROID_BACKGROUND_SYNC_MODE,
                BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY.name
            )
        return BackgroundSyncMode.values().firstOrNull { it.name == strPref }
            ?: BackgroundSyncMode.FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY
    }

    fun setPushCount(count: Int) {
        getSharedPreferences().edit { putInt(PUSH_COUNT, count) }
    }

    fun incrementPushCounter() {
        setPushCount(getPushCount() + 1)
    }

    fun getPushCount(): Int = getSharedPreferences().getInt(PUSH_COUNT, 0)

    fun useCompleteNotificationFormat(): Boolean =
        getSharedPreferences().getBoolean(USE_COMPLETE_NOTIFICATION_FORMAT, true)

    fun setUseCompleteNotificationFormatEnabled(enabled: Boolean) {
        getSharedPreferences().edit {
            putBoolean(USE_COMPLETE_NOTIFICATION_FORMAT, enabled)
        }
    }

    fun getFcmToken(): String? {
        return getSharedPreferences().getString(FCM_TOKEN, null)
    }

    fun storeFcmToken(token: String?) {
        getSharedPreferences().edit {
            putString(FCM_TOKEN, token)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "circles_preferences"
        private const val DEV_MODE_KEY = "developer_mode"
        private const val PUSH_ENDPOINT_OR_TOKEN = "push_endpoint"
        private const val PUSH_GATEWAY = "push_gateway"
        private const val FDROID_BACKGROUND_SYNC_MODE = "fdroid_background_sync_mode"
        private const val ARE_NOTIFICATIONS_ENABLED = "are_notifications_enabled"
        private const val PUSH_COUNT = "push_count"
        private const val USE_COMPLETE_NOTIFICATION_FORMAT = "use_complete_notification_format"
        private const val FCM_TOKEN = "fcm_token"
    }
}