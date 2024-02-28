package org.futo.circles.auth.feature.uia

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.R
import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.EncryptionAlgorithmHelper
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.auth.feature.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.auth.feature.token.RefreshTokenManager
import org.futo.circles.auth.feature.uia.flow.LoginStagesDataSource
import org.futo.circles.auth.model.UIANavigationEvent
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.LoadingData
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

    val subtitleLiveData: LiveData<Pair<Int, Int>> = uiaDataSource.subtitleLiveData
    val navigationLiveData = uiaDataSource.navigationLiveData
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData = restoreBackupDataSource.loadingLiveData
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

    fun onDoNotRestoreBackup() {
        navigationLiveData.postValue(UIANavigationEvent.Home)
    }

    fun finishLogin(session: Session) {
        launchBg {
            passPhraseLoadingLiveData.postValue(
                LoadingData(messageId = R.string.initial_sync, isLoading = true)
            )
            MatrixSessionProvider.awaitForSessionSync(session)
            passPhraseLoadingLiveData.postValue(LoadingData(isLoading = false))
            refreshTokenManager.scheduleTokenRefreshIfNeeded(session)
            handleKeysBackup()
            BSSpekeClientProvider.clear()
        }
    }

    fun finishSignup(session: Session) {
        launchBg {
            val result = createResult {
                MatrixInstanceProvider.matrix.authenticationService().reset()
                MatrixSessionProvider.awaitForSessionStart(session)
                preferencesProvider.setShouldShowAllExplanations()
                createPassPhraseDataSource.createPassPhraseBackup()
                BSSpekeClientProvider.clear()
            }
            (result as? Response.Success)?.let { navigationLiveData.postValue(UIANavigationEvent.ConfigureWorkspace) }
            createBackupResultLiveData.postValue(result)
        }
    }

    private suspend fun handleKeysBackup() {
        if (encryptionAlgorithmHelper.isBcryptAlgorithm()) {
            (uiaDataSource as? LoginStagesDataSource)?.userPassword?.let {
                restoreAndMigrateBCrypt(it)
            }
        } else {
            if (encryptionAlgorithmHelper.isBsSpekePassPhrase()) restoreBsSpekeBackup()
            else navigationLiveData.postValue(UIANavigationEvent.PassPhrase)
        }
    }

    private suspend fun restoreBsSpekeBackup() {
        val restoreResult = createResult { restoreBackupDataSource.restoreWithBsSpekeKey() }
        handleRestoreResult(restoreResult)
    }

    private suspend fun restoreAndMigrateBCrypt(passphrase: String) {
        val restoreResult = createResult {
            restoreBackupDataSource.restoreBcryptWithPassPhase(passphrase)
            createPassPhraseDataSource.replaceToNewKeyBackup()
        }
        handleRestoreResult(restoreResult)
    }

    private fun handleRestoreResult(restoreResult: Response<Unit>) {
        when (restoreResult) {
            is Response.Error -> {
                restoreKeysLiveData.postValue(restoreResult)
                navigationLiveData.postValue(UIANavigationEvent.PassPhrase)
            }

            is Response.Success -> navigationLiveData.postValue(UIANavigationEvent.Home)
        }
    }

}