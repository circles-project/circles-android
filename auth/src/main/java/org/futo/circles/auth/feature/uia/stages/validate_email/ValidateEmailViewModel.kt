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

    val showSubscribeCheckLiveData = MutableLiveData(dataSource.shouldShowSubscribeToEmail())

    fun sendCode(email: String, subscribeToUpdates: Boolean) {
        launchBg {
            val result = dataSource.sendValidationCode(email, subscribeToUpdates)
            sendCodeLiveData.postValue(result)
            if (result is Response.Success) showSubscribeCheckLiveData.postValue(false)
        }
    }

    fun validateEmail(code: String) {
        launchBg {
            validateEmailLiveData.postValue(dataSource.validateEmail(code))
        }
    }


}