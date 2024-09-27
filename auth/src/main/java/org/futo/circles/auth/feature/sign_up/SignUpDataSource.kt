package org.futo.circles.auth.feature.sign_up

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.sign_up.uia.SignupUIADataSource
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class SignUpDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun startNewRegistration(domain: String, username: String, password: String) =
        createResult {
            val matrix = MatrixInstanceProvider.matrix
            val service = matrix.authenticationService()
            service.cancelPendingLoginOrRegistration()

            val homeServerUrl =
                service.getLoginFlow(buildHomeServerConfigFromDomain(domain)).homeServerUrl

            val createAccountResult = service.getRegistrationWizard().createAccount(
                username, password, context.getString(R.string.initial_device_name)
            )

            val stages =
                (createAccountResult as? RegistrationResult.FlowResponse)?.flowResult?.missingStages
                    ?: throw IllegalArgumentException(
                        context.getString(R.string.new_accounts_not_available)
                    )

            SignupUIADataSource(context).startSignupUIAStages(homeServerUrl, stages)
        }

}