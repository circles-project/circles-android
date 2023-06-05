package org.futo.circles.auth.feature.sign_up.password

import androidx.lifecycle.ViewModel
import org.futo.circles.auth.base.PasswordDataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg

class PasswordViewModel(
    private val passwordDataSource: PasswordDataSource
) : ViewModel() {

    val passwordResponseLiveData = org.futo.circles.core.SingleEventLiveData<Response<Unit>>()

    fun loginWithPassword(password: String) {
        launchBg {
            passwordResponseLiveData.postValue(passwordDataSource.processPasswordStage(password))
        }
    }

}