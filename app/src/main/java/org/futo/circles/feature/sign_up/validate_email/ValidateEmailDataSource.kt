package org.futo.circles.feature.sign_up.validate_email

import org.futo.circles.core.REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE
import org.futo.circles.core.REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.core.VALIDATION_TOKEN_SUBMIT_URL_EXTENSION
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class ValidateEmailDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun sendValidationCode(email: String): Response<RegistrationResult> =
        createResult {
            wizard.registrationCustom(
                mapOf(
                    SignUpDataSource.TYPE_PARAM_KEY to REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE,
                    EMAIL_PARAM_KEY to email
                )
            )
        }


    suspend fun validateEmail(code: String): Response<RegistrationResult> {
        val result = createResult {
            wizard.registrationCustom(
                mapOf(
                    SignUpDataSource.TYPE_PARAM_KEY to REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE,
                    TOKEN_PARAM_KEY to code
                )
            )
        }
        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }

        return result
    }

    companion object {
        private const val EMAIL_PARAM_KEY = "email"
        private const val TOKEN_PARAM_KEY = "token"
    }
}