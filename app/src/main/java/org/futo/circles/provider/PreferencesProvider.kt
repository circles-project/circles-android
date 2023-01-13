package org.futo.circles.provider

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class PreferencesProvider(
    private val context: Context
) {

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

    private fun getSharedPreferences() =
        context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "Circles Preferences"
        private const val DEV_MODE_KEY = "Circles Developer mode"
        private const val PUSH_ENDPOINT_OR_TOKEN = "Circles push endpoint"
        private const val PUSH_GATEWAY = "Circles push gateway"
    }

}