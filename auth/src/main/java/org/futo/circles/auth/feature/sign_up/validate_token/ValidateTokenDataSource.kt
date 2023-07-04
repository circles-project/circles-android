package org.futo.circles.auth.feature.sign_up.validate_token

import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_TOKEN_TYPE
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class ValidateTokenDataSource @Inject constructor(
    private val signUpDataSource: SignUpDataSource
) {

    suspend fun validateToken(token: String): Response<RegistrationResult> =
        signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_TOKEN_TYPE,
                TOKEN_PARAM_KEY to token
            )
        )

    companion object {
        private const val TOKEN_PARAM_KEY = "token"
    }
}