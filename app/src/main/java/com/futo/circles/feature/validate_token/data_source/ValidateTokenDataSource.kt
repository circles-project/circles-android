package com.futo.circles.feature.validate_token.data_source

import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.io.service.CirclesSignUpService
import org.matrix.android.sdk.api.auth.registration.Stage

class ValidateTokenDataSource(
    private val service: CirclesSignUpService,
    private val signUpDataSource: SignUpDataSource
) {

    suspend fun validateToken(token: String) = createResult {
        val type = (signUpDataSource.getCurrentStage() as? Stage.Other)?.type
            ?: throw IllegalArgumentException()

        service.validateSignUpToken(
            type = type,
            token = token,
            session = signUpDataSource.getPendingSessionId()
        )
    }
}