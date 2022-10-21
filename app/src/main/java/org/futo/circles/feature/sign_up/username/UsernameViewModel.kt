package org.futo.circles.feature.sign_up.username

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class UsernameViewModel(
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