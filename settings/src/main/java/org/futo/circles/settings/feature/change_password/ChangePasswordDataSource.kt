package org.futo.circles.settings.feature.change_password

import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.BCRYPT_ALGORITHM_BACKUP
import javax.inject.Inject

class ChangePasswordDataSource @Inject constructor(
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val restoreBackupDataSource: RestoreBackupDataSource
) {

    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData

    suspend fun changePassword(oldPassword: String, newPassword: String) = createResult {
        MatrixSessionProvider.currentSession?.accountService()
            ?.changePassword(oldPassword, newPassword)
    }

    suspend fun changePasswordUIA(authConfirmationProvider: AuthConfirmationProvider) =
        createResult {
            MatrixSessionProvider.currentSession?.accountService()
                ?.changePasswordStages(authConfirmationProvider)
        }

    suspend fun createNewBackupInNeeded(newPassword: String): Response<Unit> {
        val algorithm = restoreBackupDataSource.getEncryptionAlgorithm()
        val createBackupResult = if (algorithm == BCRYPT_ALGORITHM_BACKUP) {
            createResult {
                createPassPhraseDataSource.replacePassPhraseBackup(
                    MatrixSessionProvider.currentSession?.myUserId ?: "", newPassword
                )
            }
        } else Response.Success(Unit)

        return createBackupResult
    }
}