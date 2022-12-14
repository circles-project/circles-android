package org.futo.circles.feature.log_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.log_in.switch_user.SwitchUserDataSource

class LogInViewModel(
    private val loginDataSource: LoginDataSource,
    private val switchUserDataSource: SwitchUserDataSource
) : ViewModel() {

    val loginResultLiveData = SingleEventLiveData<Response<Unit>>()
    val switchUsersLiveData = MutableLiveData(switchUserDataSource.getSwitchUsersList())

    fun startLogInFlow(userName: String, domain: String) {
        launchBg {
            val loginResult = loginDataSource.startLogin(userName, domain)
            loginResultLiveData.postValue(loginResult)
        }
    }

    fun removeSwitchUser(id: String) {
        switchUsersLiveData.postValue(switchUserDataSource.removeSwitchUser(id))
    }

    fun resumeSwitchUserSession(id: String) {

    }

}