package org.futo.circles.feature.log_in.stages

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg


class LoginStagesViewModel(
    private val loginStagesDataSource: LoginStagesDataSource
) : ViewModel() {

    val subtitleLiveData = loginStagesDataSource.subtitleLiveData
    val loginStageNavigationLiveData = loginStagesDataSource.loginStageNavigationLiveData
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val loginNavigationLiveData = loginStagesDataSource.loginNavigationLiveData
    val passPhraseLoadingLiveData = loginStagesDataSource.passPhraseLoadingLiveData
    val spacesTreeLoadingLiveData = loginStagesDataSource.spacesTreeLoadingLiveData
    val messageEventLiveData = loginStagesDataSource.messageEventLiveData

    fun restoreBackup(passphrase: String) {
        launchBg {
            restoreKeysLiveData.postValue(loginStagesDataSource.restoreBackup(passphrase))
        }
    }

    fun restoreBackup(uri: Uri) {
        launchBg {
            restoreKeysLiveData.postValue(loginStagesDataSource.restoreBackup(uri))
        }
    }

    fun onDoNotRestoreBackup() {
        launchBg { loginStagesDataSource.createSpacesTreeIfNotExist() }
    }

}