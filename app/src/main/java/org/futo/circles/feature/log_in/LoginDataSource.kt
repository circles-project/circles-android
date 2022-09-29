package org.futo.circles.feature.log_in

import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromUserId
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
        authService.cancelPendingLoginOrRegistration()
        val homeServerUrl = authService.initiateAuth(buildHomeServerConfigFromUserId(userName))
        val loginFlow = authService.getLoginFlow()
        loginStagesDataSource.startLoginStages(
            loginFlow.supportedLoginTypes,
            userName
        )
    }
}