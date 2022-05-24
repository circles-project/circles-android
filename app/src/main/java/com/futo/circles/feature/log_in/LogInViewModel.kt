package com.futo.circles.feature.log_in

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.auth.data.LoginFlowResult
import org.matrix.android.sdk.api.session.Session

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    val loginResultLiveData = SingleEventLiveData<Response<Session>>()
    val restoreKeysLiveData = SingleEventLiveData<Response<Unit>>()
    val signUpEventResultLiveData = SingleEventLiveData<Response<LoginFlowResult>>()
    val passPhraseLoadingLiveData = loginDataSource.passPhraseLoadingLiveData

    fun logIn(name: String, password: String) {
        launchBg {
            val loginResult = loginDataSource.logIn(name, password)
            loginResultLiveData.postValue(loginResult)
            (loginResult as? Response.Success)?.let {
                restoreKeysLiveData.postValue(loginDataSource.restoreKeys(password))
            }
        }
    }

    fun startSignUp() {
        launchBg {
            val response = loginDataSource.startSignUp()
            signUpEventResultLiveData.postValue(response)
        }
    }
}