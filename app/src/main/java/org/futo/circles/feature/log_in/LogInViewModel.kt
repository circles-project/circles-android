package org.futo.circles.feature.log_in

import androidx.lifecycle.ViewModel
import org.futo.circles.R
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.crypto.MXCRYPTO_ALGORITHM_MEGOLM_BACKUP
import org.matrix.android.sdk.api.session.Session

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase }

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    val loginNavigationLiveData =
        SingleEventLiveData<LoginNavigationEvent>()
    val loginResultLiveData = SingleEventLiveData<Response<Session>>()
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData = loginDataSource.passPhraseLoadingLiveData
    val messageEventLiveData = SingleEventLiveData<Int>()

    fun logIn(name: String, password: String) {
        launchBg {
            val loginResult = loginDataSource.logIn(name, password)
            loginResultLiveData.postValue(loginResult)
            (loginResult as? Response.Success)?.let { handleKeysBackup(password) }
        }
    }

    private suspend fun handleKeysBackup(password: String) {
        val algorithm = loginDataSource.getEncryptionAlgorithm()

        when (algorithm) {
            MXCRYPTO_ALGORITHM_MEGOLM_BACKUP ->
                loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)
            null -> {
                messageEventLiveData.postValue(R.string.no_backup_message)
                handleCirclesTree()
            }
            else -> restoreBackup(password)
        }
    }

    fun restoreBackup(password: String) = launchBg {
        val restoreResult = loginDataSource.restoreKeys(password)
        restoreKeysLiveData.postValue(restoreResult)
        when (restoreResult) {
            is Response.Error -> loginNavigationLiveData.postValue(LoginNavigationEvent.PassPhrase)
            is Response.Success -> handleCirclesTree()
        }
    }

    fun handleCirclesTree() = launchBg {
        val isCirclesCreated = loginDataSource.isCirclesTreeCreated()

        if (!isCirclesCreated) loginDataSource.createSpacesTree()

        loginNavigationLiveData.postValue(
            if (isCirclesCreated) LoginNavigationEvent.Main
            else LoginNavigationEvent.SetupCircles
        )
    }
}