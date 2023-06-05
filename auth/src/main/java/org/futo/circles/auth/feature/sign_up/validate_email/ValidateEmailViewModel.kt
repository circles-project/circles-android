package org.futo.circles.auth.feature.sign_up.validate_email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class ValidateEmailViewModel(
    private val dataSource: ValidateEmailDataSource
) : ViewModel() {

    val sendCodeLiveData = MutableLiveData<Response<RegistrationResult>>()
    val validateEmailLiveData =
        org.futo.circles.core.SingleEventLiveData<Response<RegistrationResult>>()


    fun sendCode(email: String) {
        launchBg {
            sendCodeLiveData.postValue(dataSource.sendValidationCode(email))
        }
    }

    fun validateEmail(code: String) {
        launchBg {
            validateEmailLiveData.postValue(dataSource.validateEmail(code))
        }
    }


}