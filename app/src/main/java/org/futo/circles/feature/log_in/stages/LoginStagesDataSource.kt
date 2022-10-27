package org.futo.circles.feature.log_in.stages

import android.content.Context
import android.net.Uri
import org.futo.circles.R
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.core.matrix.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.initialDisplayName
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase }

class LoginStagesDataSource(
    private val context: Context,
    private val restoreBackupDataSource: RestoreBackupDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) : BaseLoginStagesDataSource(context) {

    val loginNavigationLiveData = SingleEventLiveData<LoginNavigationEvent>()
    val passPhraseLoadingLiveData = restoreBackupDataSource.loadingLiveData
    val messageEventLiveData = SingleEventLiveData<Int>()

    override suspend fun performLoginStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getLoginWizard()
        val result = createResult {
            wizard.loginStageCustom(
                authParams,
                getIdentifier(),
                context.initialDisplayName()
            )
        }
        (result as? Response.Success)?.let { stageCompleted(result.data, password) }
        return result
    }

    suspend fun stageCompleted(result: RegistrationResult, password: String?) {
        if (isStageRetry(result)) return
        password?.let { userPassword = it }
        (result as? RegistrationResult.Success)?.let {
            finishLogin(it.session)
        } ?: navigateToNextStage()
    }

    private suspend fun finishLogin(session: Session) {
        MatrixSessionProvider.awaitForSessionSync(session)
        handleKeysBackup(userPassword)
    }

    private suspend fun handleKeysBackup(password: String) {
        when (restoreBackupDataSource.getEncryptionAlgorithm()) {
            MXCRYPTO_ALGORITHM_MEGOLM_BACKUP ->
                loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)
            null -> {
                messageEventLiveData.postValue(R.string.no_backup_message)
                createSpacesTreeIfNotExist()
            }
            else -> restoreBackup(password)
        }
    }

    suspend fun createSpacesTreeIfNotExist() {
        val isCirclesCreated = coreSpacesTreeBuilder.isCirclesHierarchyCreated()
        if (!isCirclesCreated) createResult { coreSpacesTreeBuilder.createCoreSpacesTree() }
        loginNavigationLiveData.postValue(
            if (isCirclesCreated) LoginNavigationEvent.Main
            else LoginNavigationEvent.SetupCircles
        )
    }

    suspend fun restoreBackup(password: String): Response<Unit> {
        val restoreResult = createResult {
            restoreBackupDataSource.restoreKeysWithPassPhase(password)
        }
        return handleRestoreResult(restoreResult)
    }

    suspend fun restoreBackup(uri: Uri): Response<Unit> {
        val restoreResult = createResult {
            restoreBackupDataSource.restoreKeysWithRecoveryKey(uri)
        }
        return handleRestoreResult(restoreResult)
    }

    private suspend fun handleRestoreResult(restoreResult: Response<Unit>): Response<Unit> {
        when (restoreResult) {
            is Response.Error -> loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)
            is Response.Success -> createSpacesTreeIfNotExist()
        }
        return restoreResult
    }
}