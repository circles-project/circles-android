package org.futo.circles.feature.sign_up.sign_up_type

import android.content.Context
import android.net.Uri
import org.futo.circles.R
import org.futo.circles.core.utils.HomeServerUtils
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SelectSignUpTypeDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    fun clearSubtitle() {
        signUpDataSource.clearSubtitle()
    }

    suspend fun startNewRegistration(
        name: String,
        password: String,
        domain: String,
        isSubscription: Boolean
    ) =
        createResult {
            val homeServerUrl = HomeServerUtils.getHomeServerUrlFromDomain(domain)
            authService.cancelPendingLoginOrRegistration()
            authService.getLoginFlow(buildHomeServerConfig(homeServerUrl))
            (authService.getRegistrationWizard().createAccount(
                name, password,
                context.getString(
                    R.string.initial_device_name,
                    context.getString(R.string.app_name)
                )
            )
                    as? RegistrationResult.FlowResponse)
                ?.let {
                    signUpDataSource.startSignUpStages(
                        it.flowResult.missingStages,
                        name,
                        password,
                        homeServerUrl
                    )
                }
        }

    private fun buildHomeServerConfig(url: String): HomeServerConnectionConfig {
        return HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(url))
            .build()
    }

}