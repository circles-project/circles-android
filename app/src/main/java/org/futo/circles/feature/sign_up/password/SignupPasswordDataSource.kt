package org.futo.circles.feature.sign_up.password

import org.futo.circles.core.REGISTRATION_PASSWORD_TYPE
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider

class SignupPasswordDataSource(
    private val signUpDataSource: SignUpDataSource
) : PasswordDataSource {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    override suspend fun processPasswordStage(password: String): Response<Unit> =
        when (val result = createResult {
            wizard.registrationCustom(
                mapOf(
                    SignUpDataSource.TYPE_PARAM_KEY to REGISTRATION_PASSWORD_TYPE,
                    PASSWORD_PARAM_KEY to password
                )
            )
        }) {
            is Response.Success -> {
                signUpDataSource.setPassword(password)
                signUpDataSource.stageCompleted(result.data)
                Response.Success(Unit)
            }
            is Response.Error -> result
        }

    companion object {
        private const val PASSWORD_PARAM_KEY = "new_password"
    }
}