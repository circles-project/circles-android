package com.futo.circles.feature.log_in

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import org.matrix.android.sdk.api.auth.data.LoginFlowResult

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    val loginResultLiveData = SingleEventLiveData<Response<Unit>>()
    val signUpEventResultLiveData = SingleEventLiveData<Response<LoginFlowResult>>()
    val passPhraseLoadingLiveData = loginDataSource.passPhraseLoadingLiveData

    fun logIn(name: String, password: String) {
        launchBg {
            val response = loginDataSource.logIn(name, password)
            loginResultLiveData.postValue(response)
        }
    }

    fun startSignUp() {
        launchBg {
            val response = loginDataSource.startSignUp()
            signUpEventResultLiveData.postValue(response)
        }
    }
}