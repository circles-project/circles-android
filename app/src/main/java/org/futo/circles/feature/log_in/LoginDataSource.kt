package org.futo.circles.feature.log_in

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.DIRECT_LOGIN_PASSWORD_TYPE
import org.futo.circles.core.LOGIN_PASSWORD_TYPE
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.auth.registration.Stage

class LoginDataSource(
    private val context: Context,
    private val loginStagesDataSource: LoginStagesDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    suspend fun startLogin(
        userName: String,
        domain: String
    ) = createResult {
        authService.cancelPendingLoginOrRegistration()
        val homeServerConfig = buildHomeServerConfigFromDomain(domain)
        authService.initiateAuth(homeServerConfig)
        val stages = prepareLoginStages(homeServerConfig)
        loginStagesDataSource.startLoginStages(stages, userName, domain)
    }

    private suspend fun prepareLoginStages(homeServerConfig: HomeServerConnectionConfig): List<Stage> {
        val flows = authService.getLoginWizard().getAllLoginFlows()
        val stages = if (flows.isEmpty()) {
            val supportedLoginMethods =
                authService.getLoginFlow(homeServerConfig).supportedLoginTypes
            if (supportedLoginMethods.contains(LOGIN_PASSWORD_TYPE))
                listOf(Stage.Other(true, DIRECT_LOGIN_PASSWORD_TYPE, null))
            else
                throw IllegalArgumentException(context.getString(R.string.unsupported_login_method))
        } else {
            flows.firstOrNull()
                ?: throw IllegalArgumentException(context.getString(R.string.unsupported_login_method))
        }
        return stages
    }
}