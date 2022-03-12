package com.futo.circles.feature.sign_up_type.data_source

import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SelectSignUpTypeDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    private val registrationWizard by lazy { authService.getRegistrationWizard() }

    fun clearSubtitle(){
        signUpDataSource.clearSubtitle()
    }

    suspend fun startNewRegistration() = createResult {
            authService.cancelPendingLoginOrRegistration()
            (registrationWizard.createAccount(null, null, null)
                    as? RegistrationResult.FlowResponse)
                ?.let {
                    signUpDataSource.startSignUpStages(it.flowResult.missingStages)
                }
        }

}