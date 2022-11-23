package org.futo.circles.provider

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class PreferencesProvider(
    private val context: Context
) {

    fun setDeveloperMode(isEnabled: Boolean) {
        getSharedPreferences().edit { this.putBoolean(DEV_MODE_KEY, isEnabled) }
    }

    fun isDeveloperModeEnabled(): Boolean = getSharedPreferences().getBoolean(DEV_MODE_KEY, false)

    private fun getSharedPreferences() =
        context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "Circles Preferences"
        private const val DEV_MODE_KEY = "Circles Developer mode"
    }

}