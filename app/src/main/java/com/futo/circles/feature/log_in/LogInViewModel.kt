package com.futo.circles.feature.log_in

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import org.matrix.android.sdk.api.session.Session

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    var loginResultLiveData = SingleEventLiveData<Response<Session>>()
    var signUpEventResultLiveData = SingleEventLiveData<Response<Unit?>>()

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