package org.futo.circles.feature.people

import org.futo.circles.core.utils.getSharedCircleFor
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider

class UserOptionsDataSource {

    val ignoredUsersLiveData =
        MatrixSessionProvider.currentSession?.userService()?.getIgnoredUsersLive()

    suspend fun ignoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.userService()?.ignoreUserIds(listOf(userId))
    }

    suspend fun unIgnoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.userService()?.unIgnoreUserIds(listOf(userId))
    }

    fun amIFollowingUser(userId: String): Boolean = getSharedCircleFor(userId) != null

    suspend fun unFollowUser(userId: String): Response<Unit?> = createResult {
        MatrixSessionProvider.currentSession?.roomService()
            ?.leaveRoom(getSharedCircleFor(userId)?.roomId ?: "")
    }
}