package org.futo.circles.feature.sign_up.password

import org.futo.circles.core.REGISTRATION_PASSWORD_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.matrix.android.sdk.api.auth.registration.Stage

class SignupPasswordDataSource(
    private val signUpDataSource: SignUpDataSource
) : PasswordDataSource {

    override fun getMinimumPasswordLength(): Int =
        ((signUpDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            MINIMUM_LENGTH_KEY, 1.0
        ) as? Double)?.toInt() ?: 1


    override suspend fun processPasswordStage(password: String): Response<Unit> =
        when (val result = signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_PASSWORD_TYPE,
                PASSWORD_PARAM_KEY to password
            ), password = password
        )) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }

    companion object {
        private const val PASSWORD_PARAM_KEY = "new_password"
        private const val MINIMUM_LENGTH_KEY = "minimum_length"
    }
}