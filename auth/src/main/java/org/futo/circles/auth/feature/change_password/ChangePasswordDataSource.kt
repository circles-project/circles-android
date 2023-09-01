package org.futo.circles.auth.feature.change_password

import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.EncryptionAlgorithmHelper
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class ChangePasswordDataSource @Inject constructor(
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val encryptionAlgorithmHelper: EncryptionAlgorithmHelper
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

    suspend fun createNewBackupInNeeded(): Response<Unit> =
        createResult {
            if (encryptionAlgorithmHelper.isBcryptAlgorithm()) createPassPhraseDataSource.replaceToNewKeyBackup()
            else if (encryptionAlgorithmHelper.isBsSpekePassPhrase())
                createPassPhraseDataSource.changeBsSpekePassword4SKey()

            BSSpekeClientProvider.clear()
        }
}