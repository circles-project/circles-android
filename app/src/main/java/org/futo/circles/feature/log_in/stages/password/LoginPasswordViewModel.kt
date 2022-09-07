package org.futo.circles.feature.log_in.stages.password

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.session.Session

class LoginPasswordViewModel(
    private val loginPasswordDataSource: LoginPasswordDataSource
) : ViewModel() {

    val loginResponseLiveData = SingleEventLiveData<Response<Session>>()

    fun loginWithPassword(password: String) {
        launchBg {
            loginResponseLiveData.postValue(loginPasswordDataSource.logIn(password))
        }
    }

}