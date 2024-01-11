package org.futo.circles.auth.feature.log_in

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
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
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class LoginDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val loginStagesDataSource: LoginStagesDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    suspend fun startLogin(
        userName: String,
        domain: String
    ) = createResult {
        authService.cancelPendingLoginOrRegistration()
        val stages = prepareLoginStages(userName, domain)
        loginStagesDataSource.startLoginStages(stages, userName, domain)
    }

    private suspend fun prepareLoginStages(
        userName: String,
        domain: String
    ): List<Stage> {
        val homeServerConfig = buildHomeServerConfigFromDomain(domain)
        val supportedLoginMethods = authService.getLoginFlow(homeServerConfig).supportedLoginTypes

        return if (supportedLoginMethods.isEmpty()) {
            getCircleLoginStages(userName, domain)
        } else if (isPasswordLogin(supportedLoginMethods)) {
            listOf(Stage.Other(true, DIRECT_LOGIN_PASSWORD_TYPE, null))
        } else {
            throw IllegalArgumentException(context.getString(R.string.unsupported_login_method))
        }
    }

    private fun isPasswordLogin(methods: List<String>) = methods.contains(LOGIN_PASSWORD_TYPE)

    private suspend fun getCircleLoginStages(userName: String, domain: String): List<Stage> {
        val identifierParams = mapOf(
            USER_PARAM_KEY to "@$userName:$domain",
            TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
        )
        val flows = authService.getLoginWizard()
            .getAllLoginFlows(identifierParams, context.getString(R.string.initial_device_name))
        return flows.firstOrNull()
            ?: throw IllegalArgumentException(context.getString(R.string.unsupported_login_method))
    }
}