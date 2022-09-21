package org.futo.circles.feature.log_in.stages

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.session.Session

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase, Password, Terms }

class LoginStagesDataSource(
    private val context: Context,
    private val restorePassPhraseDataSource: RestorePassPhraseDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) {

    val subtitleLiveData = MutableLiveData<String>()
    val passPhraseLoadingLiveData = restorePassPhraseDataSource.loadingLiveData
    val loginNavigationLiveData = SingleEventLiveData<LoginNavigationEvent>()
    val messageEventLiveData = SingleEventLiveData<Int>()

    var userName: String = ""
        private set
    var currentHomeServerUrl: String = ""
        private set

    private val stagesToComplete = mutableListOf<String>()
    var currentStage: String? = null
        private set

    private var userPassword: String = ""

    fun startLoginStages(
        supportedLoginTypes: List<String>,
        userName: String,
        homeServerUrl: String
    ) {
        this.userName = userName
        currentHomeServerUrl = homeServerUrl
        userPassword = ""
        currentStage = null
        stagesToComplete.clear()
        stagesToComplete.addAll(supportedLoginTypes)
        navigateToNextStage()
    }

    suspend fun stageCompleted(result: RegistrationResult?, password: String? = null) {
        password?.let { userPassword = it }
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

    private fun navigateToNextStage() {
        //TODO("Change for real stages implementation")
//        val stage = currentStage?.let {
//            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
//        } ?: stagesToComplete.firstOrNull()
        val stage = stagesToComplete.firstOrNull { it.endsWith("password") }

        currentStage = stage

        val event = if (stage?.endsWith("password") == true) {
            LoginNavigationEvent.Password
        } else if (stage?.endsWith("terms") == true) {
            LoginNavigationEvent.Terms
        } else {
            throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }
        loginNavigationLiveData.postValue(event)
        updatePageSubtitle()
    }

    private fun updatePageSubtitle() {
        //TODO("Change for real stages size")
        val size = 1
        //val number = getCurrentStageIndex() + 1
        val number = 1
        val subtitle = context.getString(R.string.sign_up_stage_subtitle_format, number, size)
        subtitleLiveData.postValue(subtitle)
    }

    private suspend fun handleKeysBackup() {
        when (restorePassPhraseDataSource.getEncryptionAlgorithm()) {
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
            restorePassPhraseDataSource.restoreKeysWithPassPhase(password)
        }
        return handleRestoreResult(restoreResult)
    }

    suspend fun restoreBackup(uri: Uri): Response<Unit> {
        val restoreResult = createResult {
            restorePassPhraseDataSource.restoreKeysWithRecoveryKey(uri)
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