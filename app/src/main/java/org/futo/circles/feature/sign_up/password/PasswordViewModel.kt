package org.futo.circles.feature.sign_up.password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class PasswordViewModel(
    private val passwordDataSource: PasswordDataSource
) : ViewModel() {

    val passwordResponseLiveData = SingleEventLiveData<Response<Unit>>()
    val minimumPasswordLengthLiveData =
        MutableLiveData(passwordDataSource.getMinimumPasswordLength())

    fun loginWithPassword(password: String) {
        launchBg {
            passwordResponseLiveData.postValue(passwordDataSource.processPasswordStage(password))
        }
    }

}