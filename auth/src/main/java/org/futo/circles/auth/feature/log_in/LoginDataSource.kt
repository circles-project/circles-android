package org.futo.circles.auth.feature.log_in

import android.content.Context
import org.futo.circles.auth.R
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.DIRECT_LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.LOGIN_PASSWORD_USER_ID_TYPE
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.USER_PARAM_KEY
import org.futo.circles.auth.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
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
        val stages = prepareLoginStages(homeServerConfig, userName, domain)
        loginStagesDataSource.startLoginStages(stages, userName, domain)
    }

    private suspend fun prepareLoginStages(
        homeServerConfig: HomeServerConnectionConfig,
        userName: String,
        domain: String
    ): List<Stage> {
        val identifierParams = mapOf(
            USER_PARAM_KEY to "@$userName:$domain",
            TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
        )
        val flows =
            authService.getLoginWizard()
                .getAllLoginFlows(identifierParams, context.getString(R.string.initial_device_name))
        val stages = if (flows.isEmpty()) {
            val supportedLoginMethods = try {
                authService.getLoginFlow(homeServerConfig).supportedLoginTypes
            } catch (e: Throwable) {
                throw IllegalArgumentException(context.getString(R.string.not_found_login_flow_for_user))
            }
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