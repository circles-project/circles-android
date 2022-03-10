package com.futo.circles.feature.log_in

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.extensions.launchUi
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.session.Session

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    var loginResultLiveData = MutableLiveData<Response<Session>>()

    fun logIn(name: String, password: String) {
        launchBg {
            val response = loginDataSource.logIn(name, password)
            loginResultLiveData.postValue(response)
        }
    }
}