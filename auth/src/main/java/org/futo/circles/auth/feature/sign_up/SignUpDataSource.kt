package org.futo.circles.auth.feature.sign_up

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.SUBSCRIPTION_FREE_TYPE
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.AuthenticationService
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class SignUpDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uiaFactory: UIADataSource.Factory
) {

    suspend fun startNewRegistration(domain: String) = createResult {
        initAuthServiceForDomain(domain)
        val stages = getAuthStagesFor(domain) ?: throw IllegalArgumentException(
            context.getString(R.string.new_accounts_not_available)
        )
        val uiaDataSource = UIADataSourceProvider.create(UIAFlowType.Signup, uiaFactory)
        uiaDataSource.startUIAStages(stages, domain)
    }

    private suspend fun initAuthServiceForDomain(domain: String): AuthenticationService {
        val service = MatrixInstanceProvider.matrix.authenticationService().apply {
            cancelPendingLoginOrRegistration()
            initiateAuth(buildHomeServerConfigFromDomain(domain))
        }
        return service
    }

    private suspend fun getAuthStagesFor(domain: String): List<Stage>? {
        val authService = initAuthServiceForDomain(domain)
        val flows = authService.getRegistrationWizard().getAllRegistrationFlows()
        return getFreeSignupStages(flows)
    }

    // Must contain org.futo.subscriptions.free_forever
    private fun getFreeSignupStages(flows: List<List<Stage>>): List<Stage>? =
        flows.firstOrNull { stages ->
            stages.firstOrNull { stage ->
                (stage as? Stage.Other)?.type == SUBSCRIPTION_FREE_TYPE
            } != null
        }

}