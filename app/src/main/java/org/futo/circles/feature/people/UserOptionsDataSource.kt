package org.futo.circles.feature.people

import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.workspace.SharedCircleDataSource
import javax.inject.Inject

class UserOptionsDataSource @Inject constructor(
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    val ignoredUsersLiveData =
        MatrixSessionProvider.currentSession?.userService()?.getIgnoredUsersLive()

    suspend fun ignoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.userService()?.ignoreUserIds(listOf(userId))
    }

    suspend fun unIgnoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.userService()?.unIgnoreUserIds(listOf(userId))
    }

    fun amIFollowingUser(userId: String): Boolean =
        sharedCircleDataSource.getSharedCircleFor(userId) != null

    suspend fun unFollowUser(userId: String): Response<Unit?> = createResult {
        MatrixSessionProvider.currentSession?.roomService()
            ?.leaveRoom(sharedCircleDataSource.getSharedCircleFor(userId)?.roomId ?: "")
    }
}