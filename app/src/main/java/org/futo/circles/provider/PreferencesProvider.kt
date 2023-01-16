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

    fun areNotificationEnabledForDevice(): Boolean =
        getSharedPreferences().getBoolean(ARE_NOTIFICATIONS_ENABLED, true)

    fun setNotificationEnabledForDevice(enabled: Boolean) {
        getSharedPreferences().edit {
            putBoolean(ARE_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "circles_preferences"
        private const val DEV_MODE_KEY = "developer_mode"
        private const val PUSH_ENDPOINT_OR_TOKEN = "push_endpoint"
        private const val PUSH_GATEWAY = "push_gateway"
        private const val FDROID_BACKGROUND_SYNC_MODE = "fdroid_background_sync_mode"
        private const val ARE_NOTIFICATIONS_ENABLED = "are_notifications_enabled"
    }
}