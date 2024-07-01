package org.futo.circles.auth.feature.sign_up

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.SUBSCRIPTION_FREE_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.SUBSCRPTION_GOOGLE_TYPE
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.DomainSignupFlows
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.core.base.CirclesAppConfig
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
        val flows = getAuthFlowsFor(domain)
        val stages = flows.subscriptionStages ?: flows.freeStages ?: throw IllegalArgumentException(
            context.getString(R.string.wrong_signup_config)
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

    private suspend fun getAuthFlowsFor(domain: String): DomainSignupFlows {
        val authService = initAuthServiceForDomain(domain)
        val flows = authService.getRegistrationWizard().getAllRegistrationFlows()
        val subscriptionStages = getSubscriptionSignupStages(flows)
        val freeStages = getFreeSignupStages(flows)
        return DomainSignupFlows(domain, freeStages, subscriptionStages)
    }

    // Must contain org.futo.subscriptions.free_forever
    private fun getFreeSignupStages(flows: List<List<Stage>>): List<Stage>? =
        flows.firstOrNull { stages ->
            stages.firstOrNull { stage ->
                (stage as? Stage.Other)?.type == SUBSCRIPTION_FREE_TYPE
            } != null
        }

    // Must contain org.futo.subscription.google_play, available only for gPlay flavor
    private fun getSubscriptionSignupStages(flows: List<List<Stage>>): List<Stage>? =
        if (CirclesAppConfig.isGplayFlavor()) {
            flows.firstOrNull { stages ->
                stages.firstOrNull { stage ->
                    (stage as? Stage.Other)?.type == SUBSCRPTION_GOOGLE_TYPE
                } != null
            }
        } else null

}