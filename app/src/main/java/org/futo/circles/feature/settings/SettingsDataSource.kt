package org.futo.circles.feature.settings

import org.futo.circles.auth.feature.change_password.ChangePasswordDataSource
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    private val changePasswordDataSource: ChangePasswordDataSource,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session = MatrixSessionProvider.getSessionOrThrow()
    val passPhraseLoadingLiveData = changePasswordDataSource.passPhraseLoadingLiveData
    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    suspend fun deactivateAccount(): Response<Unit> = createResult {
        session.accountService().deactivateAccount(false, authConfirmationProvider)
    }

    suspend fun changePasswordUIA() =
        changePasswordDataSource.changePasswordUIA(authConfirmationProvider)

    suspend fun createNewBackupIfNeeded() = changePasswordDataSource.createNewBackupInNeeded(
        authConfirmationProvider.getOldPassword(),
        authConfirmationProvider.getNewChangedPassword()
    )
}