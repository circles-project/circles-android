package org.futo.circles.core.utils

object HomeServerUtils {

    private const val MATRIX_URL_PREFIX = "https://matrix."

    fun getHomeServerUrlFromUserName(username: String): String {
        val domain = username.substringAfter(":")
        return getHomeServerUrlFromDomain(domain)
    }

    fun getHomeServerUrlFromDomain(domain: String): String {
        return "$MATRIX_URL_PREFIX$domain/"
    }
}