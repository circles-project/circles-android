package org.futo.circles.feature.settings.change_password

import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.matrix.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.crypto.BCRYPT_ALGORITHM_BACKUP

class ChangePasswordDataSource(
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