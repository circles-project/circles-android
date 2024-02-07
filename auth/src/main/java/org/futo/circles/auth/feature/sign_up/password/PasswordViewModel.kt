package org.futo.circles.auth.feature.sign_up.password

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.base.PasswordDataSource
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passwordDataSource: PasswordDataSource
) : ViewModel() {

    private var isPasswordWarningConfirmed: Boolean = false
    val passwordResponseLiveData = SingleEventLiveData<Response<Unit>>()

    fun loginWithPassword(password: String) {
        launchBg {
            passwordResponseLiveData.postValue(passwordDataSource.processPasswordStage(password))
        }
    }

    fun isPasswordWarningConfirmed() = isPasswordWarningConfirmed
    fun confirmPasswordWarning() {
        isPasswordWarningConfirmed = true
    }

}