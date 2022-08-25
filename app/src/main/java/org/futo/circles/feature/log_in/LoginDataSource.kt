package org.futo.circles.feature.log_in

import org.futo.circles.core.utils.HomeServerUtils
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfig
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.provider.MatrixInstanceProvider

class LoginDataSource(
    private val loginStagesDataSource: LoginStagesDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    suspend fun startLogin(
        userName: String
    ) = createResult {
        val homeServerUrl = HomeServerUtils.getHomeServerUrlFromUserName(userName)
        authService.cancelPendingLoginOrRegistration()
        val loginFlow = authService.getLoginFlow(buildHomeServerConfig(homeServerUrl))
        loginStagesDataSource.startLoginStages(
            loginFlow.supportedLoginTypes, userName, homeServerUrl
        )
    }
}