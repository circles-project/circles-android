package org.futo.circles.auth.feature.sign_up.validate_token

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

@HiltViewModel
class ValidateTokenViewModel @Inject constructor(
    private val dataSource: ValidateTokenDataSource
) : ViewModel() {

    val validateLiveData = org.futo.circles.core.SingleEventLiveData<Response<RegistrationResult>>()

    fun validateToken(token: String) {
        launchBg {
            validateLiveData.postValue(dataSource.validateToken(token))
        }
    }

}