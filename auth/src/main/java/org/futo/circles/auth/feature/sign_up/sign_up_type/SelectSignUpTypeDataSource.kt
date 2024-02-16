package org.futo.circles.auth.feature.sign_up.sign_up_type

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.sign_up.SignUpDataSource
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_FREE_TYPE
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_SUBSCRIPTION_TYPE
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class SelectSignUpTypeDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    private var registrationFlowsForDomain: Pair<String, List<List<Stage>>>? = null

    fun clearSubtitle() {
        signUpDataSource.clearSubtitle()
    }

    suspend fun getAuthFlowsFor(domain: String) = createResult {
        registrationFlowsForDomain = null
        val authService = MatrixInstanceProvider.matrix.authenticationService().apply {
            cancelPendingLoginOrRegistration()
            initiateAuth(buildHomeServerConfigFromDomain(domain))
        }
        authService.getRegistrationWizard().getAllRegistrationFlows().also {
            registrationFlowsForDomain = domain to it
        }
    }

    suspend fun startNewRegistration(isSubscription: Boolean) = createResult {
        val (domain, flows) = registrationFlowsForDomain ?: throw IllegalArgumentException(
            context.getString(R.string.wrong_signup_config)
        )
        val stages = if (isSubscription) getSubscriptionSignupStages(flows)
        else getFreeSignupStages(flows)

        stages ?: throw IllegalArgumentException(context.getString(R.string.wrong_signup_config))

        signUpDataSource.startSignUpStages(stages, domain)
    }

    // Must contain org.futo.subscriptions.free_forever
    fun getFreeSignupStages(flows: List<List<Stage>>): List<Stage>? = flows.firstOrNull { stages ->
        stages.firstOrNull { stage ->
            (stage as? Stage.Other)?.type == REGISTRATION_FREE_TYPE
        } != null
    }

    // Must contain org.futo.subscription.google_play, available only for gPlay flavor
    fun getSubscriptionSignupStages(flows: List<List<Stage>>): List<Stage>? =
        if (CirclesAppConfig.isGplayFlavor()) {
            flows.firstOrNull { stages ->
                stages.firstOrNull { stage ->
                    (stage as? Stage.Other)?.type == REGISTRATION_SUBSCRIPTION_TYPE
                } != null
            }
        } else null

}