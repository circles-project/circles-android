package org.futo.circles.auth.feature.log_in.stages

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.CoreSpacesTreeBuilder
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase }

@Singleton
class LoginStagesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restoreBackupDataSource: RestoreBackupDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource
) : BaseLoginStagesDataSource(context) {

    val loginNavigationLiveData = SingleEventLiveData<LoginNavigationEvent>()
    val passPhraseLoadingLiveData = restoreBackupDataSource.loadingLiveData
    val spacesTreeLoadingLiveData = coreSpacesTreeBuilder.loadingLiveData
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
        BSSpekeClientProvider.clear()
    }

    private suspend fun handleKeysBackup() {
        createPassPhraseDataSource.migrateBcryptKeysIfNeed(userPassword)
        try {
            restoreBackupDataSource.restoreWithBsSpekeKey()
        } catch (e: Exception) {
            loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)
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