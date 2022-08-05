package org.futo.circles.feature.sign_up.validate_token

import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.feature.sign_up.SignUpDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class ValidateTokenDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun validateToken(token: String): Response<RegistrationResult> {
        val type = (signUpDataSource.currentStage as? Stage.Other)?.type ?: ""

        val result = createResult {
            wizard.registrationCustom(
                mapOf(
                    TYPE_PARAM_KEY to type,
                    TOKEN_PARAM_KEY to token
                )
            )
        }

        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }

        return result
    }

    companion object {
        private const val TOKEN_PARAM_KEY = "token"
    }
}