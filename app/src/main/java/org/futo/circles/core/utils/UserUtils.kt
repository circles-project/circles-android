package org.futo.circles.core.utils

import org.futo.circles.provider.MatrixSessionProvider

object UserUtils {

    fun removeDomainSuffix(userId: String): String {
        var shortUserId = userId
        val serverDomain = MatrixSessionProvider.currentSession?.sessionParams?.homeServerHost ?: ""
        shortUserId = userId.removeSuffix(":$serverDomain")
        val shortDomain = serverDomain.substringAfter("matrix.")
        shortUserId = userId.removeSuffix(":$shortDomain")
        return shortUserId
    }
}