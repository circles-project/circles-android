package com.futo.circles.feature.terms.data_source


import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class AcceptTermsDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun acceptTerms(): Response<RegistrationResult> {
        val result = createResult { wizard.acceptTerms() }

        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }

        return result
    }
}