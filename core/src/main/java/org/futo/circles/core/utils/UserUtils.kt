package org.futo.circles.core.utils

import org.futo.circles.core.provider.MatrixSessionProvider


object UserUtils {

    fun removeDomainSuffix(userId: String): String {
        val serverDomain = MatrixSessionProvider.currentSession?.sessionParams?.homeServerHost ?: ""
        val shortUserId: String = if (userId.endsWith(":$serverDomain"))
            userId.removeSuffix(":$serverDomain")
        else {
            val shortDomain = serverDomain.substringAfter("matrix.")
            userId.removeSuffix(":$shortDomain")
        }
        return shortUserId
    }

    fun getServerDomain(userId: String) = userId.substringAfter(":")
}