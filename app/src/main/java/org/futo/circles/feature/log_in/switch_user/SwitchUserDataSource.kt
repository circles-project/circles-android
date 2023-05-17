package org.futo.circles.feature.log_in.switch_user

import org.futo.circles.model.SwitchUserListItem
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.data.sessionId
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getUserOrDefault

class SwitchUserDataSource {

    private val authService = MatrixInstanceProvider.matrix.authenticationService()

    fun getSwitchUsersList() =
        authService.getAllAuthSessionsParams().map {
            val session = authService.createSessionFromParams(it)
            SwitchUserListItem(
                it.credentials.sessionId(),
                session,
                session.getUserOrDefault(session.myUserId)
            )
        }

    suspend fun removeSwitchUser(id: String) {
        authService.removeSession(id)
    }

    suspend fun switchToSessionWithId(id: String): Session? {
        val session = getSessionWithId(id) ?: return null
        authService.switchToSessionWithId(id)
        MatrixSessionProvider.startSession(session)
        return session
    }

    private fun getSessionWithId(id: String): Session? =
        getSwitchUsersList().firstOrNull { it.id == id }?.session

    fun getSessionCredentialsIdByUserInfo(userName: String, domain: String): String? {
        val userId = "@$userName:$domain"
        return getSwitchUsersList().firstOrNull { it.user.userId == userId }?.id
    }

}