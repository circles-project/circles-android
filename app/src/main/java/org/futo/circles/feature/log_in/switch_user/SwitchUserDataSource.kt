package org.futo.circles.feature.log_in.switch_user

import org.futo.circles.model.SwitchUserListItem
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.data.sessionId
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

    fun removeSwitchUser(id: String): List<SwitchUserListItem> {
        authService.removeSession(id)
        return getSwitchUsersList()
    }

    fun getSessionWithId(id: String) = getSwitchUsersList().firstOrNull { it.id == id }?.session

}