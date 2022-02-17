package com.futo.circles.ui.log_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.ui.log_in.data_source.LoginDataSource
import org.matrix.android.sdk.api.session.Session

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    var loginResultLiveData = MutableLiveData<Response<Session>>()

    fun logIn(name: String, password: String, secondPassword: String?) {
        launchBg {
            val response = loginDataSource.logIn(name, password, secondPassword)
            loginResultLiveData.postValue(response)
        }
    }
}