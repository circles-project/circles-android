package org.futo.circles.feature.sign_up.username

import androidx.lifecycle.MutableLiveData
import org.futo.circles.core.REGISTRATION_USERNAME_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class UsernameDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    val domainLiveData = MutableLiveData(signUpDataSource.domain)

    suspend fun processUsernameStage(username: String): Response<RegistrationResult> =
        signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_USERNAME_TYPE,
                USERNAME_PARAM_KEY to username
            ), name = username
        )

    companion object {
        private const val USERNAME_PARAM_KEY = "username"
    }
}