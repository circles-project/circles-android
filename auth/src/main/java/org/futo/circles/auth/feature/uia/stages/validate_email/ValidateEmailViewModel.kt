package org.futo.circles.auth.feature.uia.stages.validate_email

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

@HiltViewModel
class ValidateEmailViewModel @Inject constructor(
    private val dataSource: ValidateEmailDataSource
) : ViewModel() {

    val sendCodeLiveData = MutableLiveData<Response<RegistrationResult>>()
    val validateEmailLiveData = SingleEventLiveData<Response<RegistrationResult>>()

    fun sendCode(email: String) {
        launchBg {
            val result = dataSource.sendValidationCode(email, false)
            sendCodeLiveData.postValue(result)
        }
    }

    fun validateEmail(code: String) {
        launchBg {
            validateEmailLiveData.postValue(dataSource.validateEmail(code))
        }
    }


}