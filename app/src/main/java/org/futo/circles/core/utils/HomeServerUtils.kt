package org.futo.circles.core.utils

import android.net.Uri
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

object HomeServerUtils {

    private const val MATRIX_DOMAIN_PREFIX = "matrix."

    fun buildHomeServerConfigFromDomain(domain: String) = buildHomeServerConfig(
        getHomeServerUrlFromDomain(domain)
    )

    private fun getHomeServerUrlFromDomain(domain: String): String {
        var formattedDomain = domain
        if (!domain.startsWith(MATRIX_DOMAIN_PREFIX)) formattedDomain =
            MATRIX_DOMAIN_PREFIX + domain
        return "https://$formattedDomain"
    }

    private fun buildHomeServerConfig(url: String): HomeServerConnectionConfig {
        return HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(url))
            .build()
    }
}