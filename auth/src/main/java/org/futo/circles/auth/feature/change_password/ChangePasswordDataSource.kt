package org.futo.circles.auth.feature.change_password

import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.EncryptionAlgorithmHelper
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.SSSSDataSource
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class ChangePasswordDataSource @Inject constructor(
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val encryptionAlgorithmHelper: EncryptionAlgorithmHelper,
    private val ssssDataSource: SSSSDataSource
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
        BSSpekeClientProvider.initClient(
            MatrixSessionProvider.getSessionOrThrow().myUserId,
            newPassword
        )
        if (encryptionAlgorithmHelper.isBcryptAlgorithm()) createPassPhraseDataSource.replaceToNewKeyBackup()
        else if (encryptionAlgorithmHelper.isBsSpekePassPhrase()) ssssDataSource.replaceBsSpeke4SKey()
        else Response.Success(Unit)
        BSSpekeClientProvider.clear()
        return Response.Success(Unit)
    }
}