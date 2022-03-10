package com.futo.circles.feature.log_in.data_source

import android.content.Context
import android.net.Uri
import com.futo.circles.BuildConfig
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.provider.MatrixInstanceProvider
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class LoginDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    private val homeServerConnectionConfig by lazy {
        HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(BuildConfig.MATRIX_HOME_SERVER_URL))
            .build()
    }

    private val authService by lazy {
        MatrixInstanceProvider.matrix.authenticationService()
    }

    suspend fun logIn(name: String, password: String) =
        createResult {
            authService.directAuthentication(
                homeServerConnectionConfig = homeServerConnectionConfig,
                matrixId = name,
                password = password,
                initialDeviceName = context.getString(
                    R.string.initial_device_name,
                    context.getString(R.string.app_name)
                )
            ).also { MatrixSessionProvider.startSession(it) }
        }

    suspend fun startSignUp() = createResult {
        authService.getLoginFlow(homeServerConnectionConfig)
        (authService.getRegistrationWizard()
            .getRegistrationFlow() as? RegistrationResult.FlowResponse)?.let {
            signUpDataSource.startNewRegistration(it.flowResult.missingStages)
        }
    }

}