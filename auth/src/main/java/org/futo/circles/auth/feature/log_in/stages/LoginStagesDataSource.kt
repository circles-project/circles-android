package org.futo.circles.auth.feature.log_in.stages

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.CoreSpacesTreeBuilder
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.crypto.BCRYPT_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.crypto.BSSPEKE_ALGORITHM_BACKUP
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase }

@Singleton
class LoginStagesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restoreBackupDataSource: RestoreBackupDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) : BaseLoginStagesDataSource(context) {

    val loginNavigationLiveData = org.futo.circles.core.SingleEventLiveData<LoginNavigationEvent>()
    val passPhraseLoadingLiveData = restoreBackupDataSource.loadingLiveData
    val spacesTreeLoadingLiveData = coreSpacesTreeBuilder.loadingLiveData
    val messageEventLiveData = org.futo.circles.core.SingleEventLiveData<Int>()

    override suspend fun performLoginStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getLoginWizard()
        val result = createResult {
            wizard.loginStageCustom(
                authParams,
                getIdentifier(),
                context.getString(R.string.initial_device_name)
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
        handleKeysBackup()
    }

    private suspend fun handleKeysBackup() {
        when (val algo = restoreBackupDataSource.getEncryptionAlgorithm()) {
            MXCRYPTO_ALGORITHM_MEGOLM_BACKUP ->
                loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)

            BCRYPT_ALGORITHM_BACKUP, BSSPEKE_ALGORITHM_BACKUP -> restoreBackup(userPassword, algo)

            else -> {
                messageEventLiveData.postValue(R.string.no_backup_message)
                createSpacesTreeIfNotExist()
            }
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

    suspend fun restoreBackup(password: String, algo: String): Response<Unit> {
        val restoreResult = createResult {
            restoreBackupDataSource.restoreKeysWithPassPhase(password, userName, algo)
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