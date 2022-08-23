package org.futo.circles.feature.log_in

import org.futo.circles.core.utils.HomeServerUtils
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.provider.MatrixInstanceProvider

class LoginDataSource(
    private val loginStagesDataSource: LoginStagesDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    suspend fun startLogin(
        name: String
    ) = createResult {
        val homeServerUrl = HomeServerUtils.getHomeServerUrlFromDomain(domain)
        authService.cancelPendingLoginOrRegistration()
        authService.getLoginFlow(buildHomeServerConfig(homeServerUrl))
        signUpDataSource.startSignUpStages(
            it.flowResult.missingStages,
            name,
            password,
            homeServerUrl,
            isSubscription,
            subscriptionReceipt
        )
    }
}