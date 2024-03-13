package org.futo.circles.auth.feature.uia.stages.validate_token

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

@HiltViewModel
class ValidateTokenViewModel @Inject constructor(
    private val dataSource: ValidateTokenDataSource
) : ViewModel() {

    val validateLiveData = SingleEventLiveData<Response<RegistrationResult>>()

    fun validateToken(token: String) {
        launchBg {
            validateLiveData.postValue(dataSource.validateToken(token))
        }
    }

}