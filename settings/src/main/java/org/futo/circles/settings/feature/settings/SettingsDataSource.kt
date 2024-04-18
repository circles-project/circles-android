package org.futo.circles.settings.feature.settings

import org.futo.circles.settings.feature.change_password.ChangePasswordDataSource
import org.futo.circles.auth.feature.uia.flow.reauth.AuthConfirmationProvider
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

    suspend fun deactivateAccount(): Response<Unit> = createResult {
        session.accountService().deactivateAccount(true, authConfirmationProvider)
    }

    suspend fun addEmailUIA() = createResult {
        session.accountService().changeEmailStages(authConfirmationProvider)
    }

    suspend fun changePasswordUIA() =
        changePasswordDataSource.changePasswordUIA(authConfirmationProvider)

    suspend fun createNewBackupIfNeeded() = changePasswordDataSource.createNewBackupInNeeded()
}