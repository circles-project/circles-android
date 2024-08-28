package org.futo.circles.auth.feature.uia

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.R
import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.EncryptionAlgorithmHelper
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.auth.feature.token.RefreshTokenManager
import org.futo.circles.auth.model.AuthUIAScreenNavigationEvent
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject

@HiltViewModel
class UIAViewModel @Inject constructor(
    private val encryptionAlgorithmHelper: EncryptionAlgorithmHelper,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val restoreBackupDataSource: RestoreBackupDataSource,
    private val refreshTokenManager: RefreshTokenManager,
    private val preferencesProvider: PreferencesProvider
) : ViewModel() {

    val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    val stagesNavigationLiveData = uiaDataSource.stagesNavigationLiveData
    val navigationLiveData = SingleEventLiveData<AuthUIAScreenNavigationEvent>()
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData: MediatorLiveData<ResLoadingData> =
        MediatorLiveData<ResLoadingData>().also {
            it.addSource(restoreBackupDataSource.loadingLiveData) { value ->
                it.postValue(value)
            }
            it.addSource(createPassPhraseDataSource.loadingLiveData) { value ->
                it.postValue(value)
            }
        }
    val finishUIAEventLiveData = uiaDataSource.finishUIAEventLiveData
    val createBackupResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun restoreBackupWithPassPhrase(passphrase: String) {
        launchBg {
            val restoreResult = createResult {
                restoreBackupDataSource.restoreKeysWithPassPhase(passphrase)
            }
            handleRestoreResult(restoreResult)
        }
    }

    fun restoreBackupWithRawKey(rawKey: String) {
        launchBg {
            val restoreResult = createResult {
                restoreBackupDataSource.restoreKeysWithRawKey(rawKey)
            }
            handleRestoreResult(restoreResult)
        }
    }

    fun restoreBackup(uri: Uri) {
        launchBg {
            val restoreResult = createResult {
                restoreBackupDataSource.restoreKeysWithRecoveryKey(uri)
            }
            handleRestoreResult(restoreResult)
        }
    }

    fun finishLogin(session: Session) {
        launchBg {
            passPhraseLoadingLiveData.postValue(
                ResLoadingData(messageId = R.string.initial_sync, isLoading = true)
            )
            MatrixSessionProvider.awaitForSessionSync(session)
            passPhraseLoadingLiveData.postValue(ResLoadingData(isLoading = false))
            refreshTokenManager.scheduleTokenRefreshIfNeeded(session)
            handleKeysBackup()
        }
    }

    fun finishSignup(session: Session) {
        launchBg {
            val result = createResult {
                MatrixInstanceProvider.matrix.authenticationService().reset()
                MatrixSessionProvider.awaitForSessionStart(session)
                createPassPhraseDataSource.createPassPhraseBackup()
                clearProviders()
            }
            (result as? Response.Success)?.let {
                navigationLiveData.postValue(AuthUIAScreenNavigationEvent.ConfigureWorkspace)
            }
            createBackupResultLiveData.postValue(result)
        }
    }

    fun finishForgotPassword(session: Session) {
        launchBg {
            val result = createResult {
                passPhraseLoadingLiveData.postValue(
                    ResLoadingData(messageId = R.string.initial_sync, isLoading = true)
                )
                MatrixSessionProvider.awaitForSessionSync(session)
                createPassPhraseDataSource.replaceToNewKeyBackup()
            }
            (result as? Response.Success)?.let { clearProvidersAndNavigateHome() }
            createBackupResultLiveData.postValue(result)
        }
    }

    private suspend fun handleKeysBackup() {
        if (encryptionAlgorithmHelper.isBsSpekePassPhrase()) restoreBsSpekeBackup()
        else storeNotRestoredSessionAndShowPassphrase()
    }

    private suspend fun restoreBsSpekeBackup() {
        val restoreResult = createResult { restoreBackupDataSource.restoreWithBsSpekeKey() }
        handleRestoreResult(restoreResult)
    }

    private fun handleRestoreResult(restoreResult: Response<Unit>) {
        when (restoreResult) {
            is Response.Error -> {
                restoreKeysLiveData.postValue(restoreResult)
                storeNotRestoredSessionAndShowPassphrase()
            }

            is Response.Success -> clearProvidersAndNavigateHome()
        }
    }

    fun cancelRestore() {
        launchBg {
            val session = MatrixSessionProvider.currentSession ?: return@launchBg
            refreshTokenManager.cancelTokenRefreshing(session)
            MatrixSessionProvider.removeListenersAndStopSync()
            MatrixInstanceProvider.matrix.authenticationService().removeSession(session.sessionId)
            preferencesProvider.removeSessionFromNotRestored(session.sessionId)
        }
    }

    private fun storeNotRestoredSessionAndShowPassphrase() {
        MatrixSessionProvider.currentSession?.sessionId?.let {
            preferencesProvider.storeSessionAsNotRestored(it)
        }
        navigationLiveData.postValue(AuthUIAScreenNavigationEvent.PassPhrase)
    }

    private fun clearProvidersAndNavigateHome() {
        clearProviders()
        navigationLiveData.postValue(AuthUIAScreenNavigationEvent.Home)
    }

    private fun clearProviders() {
        BSSpekeClientProvider.clear()
        UIADataSourceProvider.clear()
    }

}