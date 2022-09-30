package org.futo.circles.feature.sign_up.validate_token

import org.futo.circles.core.REGISTRATION_TOKEN_TYPE
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class ValidateTokenDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    suspend fun validateToken(token: String): Response<RegistrationResult> =
        signUpDataSource.performRegistrationStage(
            mapOf(
                SignUpDataSource.TYPE_PARAM_KEY to REGISTRATION_TOKEN_TYPE,
                TOKEN_PARAM_KEY to token
            )
        )

    companion object {
        private const val TOKEN_PARAM_KEY = "token"
    }
}