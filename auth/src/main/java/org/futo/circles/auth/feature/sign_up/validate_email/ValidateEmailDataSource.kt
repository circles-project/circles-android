package org.futo.circles.auth.feature.sign_up.validate_email

import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class ValidateEmailDataSource @Inject constructor(
    private val signUpDataSource: SignUpDataSource
) {

    suspend fun sendValidationCode(email: String): Response<RegistrationResult> =
        signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE,
                EMAIL_PARAM_KEY to email
            )
        )

    suspend fun validateEmail(code: String): Response<RegistrationResult> =
        signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE,
                TOKEN_PARAM_KEY to code
            )
        )

    companion object {
        private const val EMAIL_PARAM_KEY = "email"
        private const val TOKEN_PARAM_KEY = "token"
    }
}