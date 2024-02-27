package org.futo.circles.auth.feature.log_in.stages

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class LoginStagesViewModel @Inject constructor(
    private val loginStagesDataSource: LoginStagesDataSource
) : ViewModel() {

    val subtitleLiveData = loginStagesDataSource.subtitleLiveData
    val loginStageNavigationLiveData = loginStagesDataSource.loginStageNavigationLiveData
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val loginNavigationLiveData = loginStagesDataSource.loginNavigationLiveData
    val passPhraseLoadingLiveData = loginStagesDataSource.passPhraseLoadingLiveData

    fun restoreBackupWithPassPhrase(passphrase: String) {
        launchBg {
            val result = loginStagesDataSource.restoreBackupWithPassphrase(passphrase)
            restoreKeysLiveData.postValue(result)
        }
    }

    fun restoreBackupWithRawKey(rawKey: String) {
        launchBg {
            val result = loginStagesDataSource.restoreBackupWithRawKey(rawKey)
            restoreKeysLiveData.postValue(result)
        }
    }

    fun restoreBackup(uri: Uri) {
        launchBg {
            restoreKeysLiveData.postValue(loginStagesDataSource.restoreBackup(uri))
        }
    }

    fun onDoNotRestoreBackup() {
        loginStagesDataSource.navigateToMain()
    }

}