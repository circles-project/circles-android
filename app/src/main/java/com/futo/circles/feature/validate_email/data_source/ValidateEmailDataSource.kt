package com.futo.circles.feature.validate_email.data_source

import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegisterThreePid
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class ValidateEmailDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun sendValidationCode(email: String): Response<RegistrationResult> =
        createResult { wizard.addThreePid(RegisterThreePid.Email(email)) }


    suspend fun validateEmail(code: String): Response<RegistrationResult> {
        val result = createResult { wizard.handleValidateThreePid(code) }
        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }
        return result
    }
}