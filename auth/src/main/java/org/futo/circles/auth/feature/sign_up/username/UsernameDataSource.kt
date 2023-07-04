package org.futo.circles.auth.feature.sign_up.username

import androidx.lifecycle.MutableLiveData
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_USERNAME_TYPE
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class UsernameDataSource @Inject constructor(
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