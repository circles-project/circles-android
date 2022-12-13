package org.futo.circles.feature.log_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.SwitchUserListItem
import org.futo.circles.provider.MatrixInstanceProvider

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    val loginResultLiveData = SingleEventLiveData<Response<Unit>>()
    val switchUsersLiveData = MutableLiveData<List<SwitchUserListItem>>()

    fun startLogInFlow(userName: String, domain: String) {
        launchBg {
            val loginResult = loginDataSource.startLogin(userName, domain)
            loginResultLiveData.postValue(loginResult)
        }
    }

    private fun submitSwitchUsersList() {
        val users = MatrixInstanceProvider.matrix.authenticationService().getAllAuthSessionsParams()
            .map { SwitchUserListItem(it) }
        switchUsersLiveData.postValue(users)
    }

    fun removeSwitchUser(id: String) {
        MatrixInstanceProvider.matrix.authenticationService().removeSession(id)
        submitSwitchUsersList()
    }

    fun resumeSwitchUserSession(id: String) {

    }

}