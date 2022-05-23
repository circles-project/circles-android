package com.futo.circles.feature.sign_up.sign_up_type

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SelectSignUpTypeDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    private val registrationWizard by lazy { authService.getRegistrationWizard() }

    fun clearSubtitle() {
        signUpDataSource.clearSubtitle()
    }

    suspend fun startNewRegistration(name: String, password: String) = createResult {
        authService.cancelPendingLoginOrRegistration()
        (registrationWizard.createAccount(
            name, password,
            context.getString(R.string.initial_device_name, context.getString(R.string.app_name))
        )
                as? RegistrationResult.FlowResponse)
            ?.let {
                signUpDataSource.startSignUpStages(it.flowResult.missingStages, password)
            }
    }

}