package org.futo.circles.core.feature.user

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class UserOptionsDataSource @Inject constructor() {

    val ignoredUsersLiveData =
        MatrixSessionProvider.currentSession?.userService()?.getIgnoredUsersLive()

    suspend fun ignoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.userService()?.ignoreUserIds(listOf(userId))
    }

    suspend fun unIgnoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.userService()?.unIgnoreUserIds(listOf(userId))
    }

}