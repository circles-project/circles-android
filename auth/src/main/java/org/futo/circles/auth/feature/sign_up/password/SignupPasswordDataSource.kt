package org.futo.circles.auth.feature.sign_up.password

import org.futo.circles.auth.base.PasswordDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_PASSWORD_TYPE
import org.futo.circles.core.extensions.Response
import javax.inject.Inject

class SignupPasswordDataSource @Inject constructor(
    private val signUpDataSource: SignUpDataSource
) : PasswordDataSource {

    override suspend fun processPasswordStage(password: String): Response<Unit> =
        when (val result = signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_PASSWORD_TYPE,
                PASSWORD_PARAM_KEY to password
            )
        )) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }

    companion object {
        private const val PASSWORD_PARAM_KEY = "new_password"
    }
}