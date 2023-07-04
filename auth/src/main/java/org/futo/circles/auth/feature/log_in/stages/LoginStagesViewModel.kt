package org.futo.circles.auth.feature.log_in.stages

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class LoginStagesViewModel @Inject constructor(
    private val loginStagesDataSource: LoginStagesDataSource
) : ViewModel() {

    val subtitleLiveData = loginStagesDataSource.subtitleLiveData
    val loginStageNavigationLiveData = loginStagesDataSource.loginStageNavigationLiveData
    val restoreKeysLiveData = org.futo.circles.core.SingleEventLiveData<Response<Unit>>()
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