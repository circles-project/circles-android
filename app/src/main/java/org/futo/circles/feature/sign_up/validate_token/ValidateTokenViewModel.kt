package org.futo.circles.feature.sign_up.validate_token

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class ValidateTokenViewModel(
    private val dataSource: ValidateTokenDataSource
) : ViewModel() {

    val validateLiveData = SingleEventLiveData<Response<RegistrationResult>>()

    fun validateToken(token: String) {
        launchBg {
            validateLiveData.postValue(dataSource.validateToken(token))
        }
    }

}