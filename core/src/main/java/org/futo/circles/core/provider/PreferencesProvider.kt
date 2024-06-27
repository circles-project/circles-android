package org.futo.circles.core.provider

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferencesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getSharedPreferences(): SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE)

    fun setDeveloperMode(isEnabled: Boolean) {
        getSharedPreferences().edit { putBoolean(DEV_MODE_KEY, isEnabled) }
    }

    fun isDeveloperModeEnabled(): Boolean = getSharedPreferences().getBoolean(DEV_MODE_KEY, false)

    fun getFcmToken(): String? {
        return getSharedPreferences().getString(FCM_TOKEN, null)
    }

    fun storeFcmToken(token: String?) {
        getSharedPreferences().edit { putString(FCM_TOKEN, token) }
    }

    fun getEndpoint(): String? {
        return getSharedPreferences().getString(ENDPOINT_OR_TOKEN, null)
    }

    fun storeUpEndpoint(endpoint: String?) {
        getSharedPreferences().edit { putString(ENDPOINT_OR_TOKEN, endpoint) }
    }

    fun getPushGateway(): String? {
        return getSharedPreferences().getString(PUSH_GATEWAY, null)
    }

    fun storePushGateway(gateway: String?) {
        getSharedPreferences().edit { putString(PUSH_GATEWAY, gateway) }
    }

    fun setFdroidBackgroundSyncEnabled(isEnabled: Boolean) {
        getSharedPreferences().edit { putBoolean(FDROID_BACKGROUND_SYNC, isEnabled) }
    }

    fun isFdroidBackgroundSyncEnabled(): Boolean =
        getSharedPreferences().getBoolean(FDROID_BACKGROUND_SYNC, true)

    fun getWhatsNewShowedForVersion(): Int {
        return getSharedPreferences().getInt(WHATS_NEW_SHOWED_FOR, 37)
    }

    fun storeWhatsNewShowedFor(version: Int) {
        getSharedPreferences().edit { putInt(WHATS_NEW_SHOWED_FOR, version) }
    }

    fun getNotRestoredSessions(): Set<String> =
        getSharedPreferences().getStringSet(NOT_RESTORED_SESSION, emptySet()) ?: emptySet()

    fun removeSessionFromNotRestored(sessionId: String) {
        val set = getNotRestoredSessions().toMutableSet().apply { remove(sessionId) }
        getSharedPreferences().edit(true) { putStringSet(NOT_RESTORED_SESSION, set) }
    }

    fun storeSessionAsNotRestored(sessionId: String) {
        val set = getNotRestoredSessions().toMutableSet().apply { add(sessionId) }
        getSharedPreferences().edit(true) { putStringSet(NOT_RESTORED_SESSION, set) }
    }

    fun setPhotoGalleryEnabled(isEnabled: Boolean) {
        getSharedPreferences().edit { putBoolean(PHOTO_GALLERY_KEY, isEnabled) }
    }

    fun isPhotoGalleryEnabled(): Boolean =
        getSharedPreferences().getBoolean(PHOTO_GALLERY_KEY, false)

    companion object {
        private const val PREFERENCES_NAME = "circles_preferences"
        private const val DEV_MODE_KEY = "developer_mode"
        private const val FCM_TOKEN = "fcm_token"
        private const val ENDPOINT_OR_TOKEN = "unified_push_endpoint_or_token"
        private const val PUSH_GATEWAY = "push_gateway"
        private const val FDROID_BACKGROUND_SYNC = "fdroid_background_sync"
        private const val WHATS_NEW_SHOWED_FOR = "whats_new_showed_for"
        private const val NOT_RESTORED_SESSION = "not_restored_session"
        const val PHOTO_GALLERY_KEY = "photo_gallery_enabled"
    }
}