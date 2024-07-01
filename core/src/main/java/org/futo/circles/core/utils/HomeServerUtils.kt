package org.futo.circles.core.utils

import android.net.Uri
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

object HomeServerUtils {

    fun buildHomeServerConfigFromUserId(userId: String): HomeServerConnectionConfig {
        val domain = UserIdUtils.getServerDomain(userId)
        return buildHomeServerConfigFromDomain(domain)
    }


    fun buildHomeServerConfigFromDomain(domain: String) = HomeServerConnectionConfig
        .Builder()
        .withHomeServerUri(Uri.parse("https://$domain"))
        .build()

}