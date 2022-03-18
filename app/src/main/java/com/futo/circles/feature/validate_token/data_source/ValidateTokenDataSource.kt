package com.futo.circles.feature.validate_token.data_source

import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
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
            wizard.registrationOther(
                mapOf(
                    "type" to type,
                    "token" to token
                )
            )
        }

        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }

        return result
    }
}