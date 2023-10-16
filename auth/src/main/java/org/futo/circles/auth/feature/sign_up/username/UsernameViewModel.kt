package org.futo.circles.auth.feature.sign_up.username

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val usernameDataSource: UsernameDataSource
) : ViewModel() {

    val usernameResponseLiveData = SingleEventLiveData<Response<RegistrationResult>>()
    val domainLiveData = usernameDataSource.domainLiveData

    fun setUsername(username: String) {
        launchBg {
            usernameResponseLiveData.postValue(usernameDataSource.processUsernameStage(username))
        }
    }

}