package org.futo.circles.core.utils

import org.futo.circles.BuildConfig
import org.futo.circles.core.*
import org.futo.circles.provider.MatrixSessionProvider

object ConfigUtils {

    fun getRageshakeUrl(): String {
        if (BuildConfig.DEBUG) return DEBUG_RAGESHAKE_URL
        val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
        if (homeServerUrl.contains(BuildConfig.US_SERVER_DOMAIN)) return US_RAGESHAKE_URL
        if (homeServerUrl.contains(BuildConfig.EU_SERVER_DOMAIN)) return EU_RAGESHAKE_URL
        return US_RAGESHAKE_URL
    }

    fun getPusherUrl(): String {
        if (BuildConfig.DEBUG) return DEBUG_PUSHER_URL
        val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
        if (homeServerUrl.contains(BuildConfig.US_SERVER_DOMAIN)) return US_PUSHER_URL
        if (homeServerUrl.contains(BuildConfig.EU_SERVER_DOMAIN)) return EU_PUSHER_URL
        return US_PUSHER_URL
    }
}