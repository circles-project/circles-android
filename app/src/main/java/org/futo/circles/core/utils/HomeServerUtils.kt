package org.futo.circles.core.utils

import android.net.Uri
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

object HomeServerUtils {

    private const val MATRIX_URL_PREFIX = "https://matrix."

    fun getHomeServerUrlFromUserName(username: String): String {
        val domain = username.substringAfter(":")
        return getHomeServerUrlFromDomain(domain)
    }

    fun getHomeServerUrlFromDomain(domain: String): String {
        return "$MATRIX_URL_PREFIX$domain/"
    }

    fun buildHomeServerConfig(url: String): HomeServerConnectionConfig {
        return HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(url))
            .build()
    }
}