package com.futo.circles.feature.validate_token.data_source

import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.getPendingSignUpSessionId
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.io.request.ValidateSignUpTokenRequestBody
import com.futo.circles.io.request.ValidateTokenRequestParams
import com.futo.circles.io.service.CirclesSignUpService
import com.futo.circles.provider.MatrixInstanceProvider
import okhttp3.ResponseBody
import org.matrix.android.sdk.api.auth.registration.Stage

class ValidateTokenDataSource(
    private val service: CirclesSignUpService,
    private val signUpDataSource: SignUpDataSource
) {

    private val pendingSessionId by lazy {
        MatrixInstanceProvider.matrix.authenticationService()
            .getRegistrationWizard().getPendingSignUpSessionId()
    }

    suspend fun validateToken(token: String): Response<ResponseBody> {
        val result = createResult {
            val type = (signUpDataSource.currentStage as? Stage.Other)?.type
                ?: throw IllegalArgumentException()

            service.validateSignUpToken(
                ValidateSignUpTokenRequestBody(
                    ValidateTokenRequestParams(
                        type = type,
                        token = token,
                        session = pendingSessionId
                    )
                )
            )
        }

        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(null) }

        return result
    }
}