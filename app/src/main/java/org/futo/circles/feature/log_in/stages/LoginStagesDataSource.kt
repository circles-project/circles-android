package org.futo.circles.feature.log_in.stages

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.*
import org.futo.circles.core.matrix.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpNavigationEvents
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.session.Session

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase, Password, Terms, DirectPassword }

class LoginStagesDataSource(
    private val context: Context,
    private val restoreBackupDataSource: RestoreBackupDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) {

    val subtitleLiveData = MutableLiveData<String>()
    val passPhraseLoadingLiveData = restoreBackupDataSource.loadingLiveData
    val loginNavigationLiveData = SingleEventLiveData<LoginNavigationEvent>()
    val messageEventLiveData = SingleEventLiveData<Int>()

    var userName: String = ""
        private set

    private val stagesToComplete = mutableListOf<Stage>()
    var currentStage: Stage? = null
        private set

    private var userPassword: String = ""

    fun startLoginStages(
        loginStages: List<Stage>,
        userName: String
    ) {
        this.userName = userName
        userPassword = ""
        currentStage = null
        stagesToComplete.clear()
        stagesToComplete.addAll(loginStages)
        navigateToNextStage()
    }

    fun setPassword(password: String) {
        userPassword = password
    }

    suspend fun stageCompleted(result: RegistrationResult) {
        (result as? RegistrationResult.Success)?.let {
            finishLogin(it.session)
        } ?: navigateToNextStage()
    }

    private suspend fun finishLogin(session: Session) {
        MatrixSessionProvider.awaitForSessionSync(session)
        handleKeysBackup()
    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun setNextStage() {
        currentStage = currentStage?.let {
            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
        } ?: stagesToComplete.firstOrNull()
    }

    private fun navigateToNextStage() {
        setNextStage()
        val event = when (val stage = currentStage) {
            is Stage.Terms -> LoginNavigationEvent.Terms
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }
        loginNavigationLiveData.postValue(event)
        updatePageSubtitle()
    }

    private fun handleStageOther(type: String): LoginNavigationEvent = when (type) {
        LOGIN_PASSWORD_TYPE -> LoginNavigationEvent.Password
        DIRECT_LOGIN_PASSWORD_TYPE -> LoginNavigationEvent.DirectPassword
        else -> throw IllegalArgumentException(
            context.getString(R.string.not_supported_stage_format, type)
        )
    }

    private fun updatePageSubtitle() {
        val size = stagesToComplete.size
        val number = getCurrentStageIndex() + 1
        val subtitle = context.getString(R.string.sign_up_stage_subtitle_format, number, size)
        subtitleLiveData.postValue(subtitle)
    }

    private suspend fun handleKeysBackup() {
        when (restoreBackupDataSource.getEncryptionAlgorithm()) {
            MXCRYPTO_ALGORITHM_MEGOLM_BACKUP ->
                loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)
            null -> {
                messageEventLiveData.postValue(R.string.no_backup_message)
                createSpacesTreeIfNotExist()
            }
            else -> restoreBackup(userPassword)
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