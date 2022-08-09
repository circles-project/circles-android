package org.futo.circles.feature.people

import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider

class UserOptionsDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun ignoreSender(userId: String) = createResult {
        session?.userService()?.ignoreUserIds(listOf(userId))
    }

    suspend fun unIgnoreSender(userId: String) = createResult {
        session?.userService()?.unIgnoreUserIds(listOf(userId))
    }
}