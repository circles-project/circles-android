package org.futo.circles.auth.feature.sign_up.username

import androidx.lifecycle.ViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class UsernameViewModel(
    private val usernameDataSource: UsernameDataSource
) : ViewModel() {

    val usernameResponseLiveData =
        org.futo.circles.core.SingleEventLiveData<Response<RegistrationResult>>()
    val domainLiveData = usernameDataSource.domainLiveData

    fun setUsername(username: String) {
        launchBg {
            usernameResponseLiveData.postValue(usernameDataSource.processUsernameStage(username))
        }
    }

}