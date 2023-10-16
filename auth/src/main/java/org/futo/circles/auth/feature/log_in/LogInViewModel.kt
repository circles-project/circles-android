package org.futo.circles.auth.feature.log_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.auth.feature.log_in.switch_user.SwitchUserDataSource
import org.futo.circles.core.base.SingleEventLiveData
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val loginDataSource: LoginDataSource,
    private val switchUserDataSource: SwitchUserDataSource
) : ViewModel() {

    val loginResultLiveData = SingleEventLiveData<Response<Unit>>()
    val switchUsersLiveData = MutableLiveData(switchUserDataSource.getSwitchUsersList())
    val navigateToBottomMenuScreenLiveData = SingleEventLiveData<Unit>()

    fun startLogInFlow(userName: String, domain: String) {
        switchUserDataSource.getSessionCredentialsIdByUserInfo(userName, domain)
            ?.let { resumeSwitchUserSession(it) }
            ?: login(userName, domain)
    }

    fun removeSwitchUser(id: String) {
        launchBg {
            switchUserDataSource.removeSwitchUser(id)
            switchUsersLiveData.postValue(switchUserDataSource.getSwitchUsersList())
        }
    }

    fun resumeSwitchUserSession(id: String) {
        launchBg {
            switchUserDataSource.switchToSessionWithId(id)?.let {
                navigateToBottomMenuScreenLiveData.postValue(Unit)
            }
        }
    }

    private fun login(userName: String, domain: String) {
        launchBg {
            val loginResult = loginDataSource.startLogin(userName, domain)
            loginResultLiveData.postValue(loginResult)
        }
    }

}