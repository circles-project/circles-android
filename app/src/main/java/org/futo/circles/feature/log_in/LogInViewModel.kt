package org.futo.circles.feature.log_in

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    val loginResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun startLogInFlow(userName: String, domain: String) {
        launchBg {
            val loginResult = loginDataSource.startLogin(userName, domain)
            loginResultLiveData.postValue(loginResult)
        }
    }

}