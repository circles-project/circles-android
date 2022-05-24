package com.futo.circles.feature.sign_up.validate_email

import com.futo.circles.BuildConfig
import com.futo.circles.core.VALIDATION_TOKEN_SUBMIT_URL_PREFIX
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegisterThreePid
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.internal.auth.registration.AddThreePidRegistrationResponse

class ValidateEmailDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun sendValidationCode(email: String): Response<AddThreePidRegistrationResponse> =
        createResult { wizard.addThreePid(RegisterThreePid.Email(email)) }


    suspend fun validateEmail(code: String): Response<RegistrationResult> {
        val result = createResult {
            wizard.handleValidateThreePid(
                code,
                BuildConfig.MATRIX_HOME_SERVER_URL + VALIDATION_TOKEN_SUBMIT_URL_PREFIX
            )
        }
        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }
        return result
    }
}