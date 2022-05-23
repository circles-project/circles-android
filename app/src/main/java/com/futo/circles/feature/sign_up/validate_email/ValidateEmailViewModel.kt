package com.futo.circles.feature.sign_up.validate_email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.internal.auth.registration.AddThreePidRegistrationResponse

class ValidateEmailViewModel(
    private val dataSource: ValidateEmailDataSource
) : ViewModel() {

    val sendCodeLiveData = MutableLiveData<Response<AddThreePidRegistrationResponse>>()
    val validateEmailLiveData = SingleEventLiveData<Response<RegistrationResult>>()


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