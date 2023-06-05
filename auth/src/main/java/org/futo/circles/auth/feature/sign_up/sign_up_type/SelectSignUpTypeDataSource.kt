package org.futo.circles.auth.feature.sign_up.sign_up_type

import android.content.Context
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.sign_up.SignUpDataSource.Companion.REGISTRATION_SUBSCRIPTION_TYPE
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.registration.Stage

class SelectSignUpTypeDataSource(
    private val context: Context,
    private val signUpDataSource: org.futo.circles.auth.feature.sign_up.SignUpDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    fun clearSubtitle() {
        signUpDataSource.clearSubtitle()
    }

    suspend fun startNewRegistration(
        domain: String,
        isSubscription: Boolean,
        subscriptionReceiptData: SubscriptionReceiptData?
    ) = createResult {
        authService.cancelPendingLoginOrRegistration()
        authService.initiateAuth(buildHomeServerConfigFromDomain(domain))
        val flows = authService.getRegistrationWizard().getAllRegistrationFlows()
        signUpDataSource.startSignUpStages(
            prepareStagesList(isSubscription, flows),
            domain,
            subscriptionReceiptData
        )
    }

    private fun prepareStagesList(isSubscription: Boolean, flows: List<List<Stage>>): List<Stage> =
        if (isSubscription) {
            flows.firstOrNull {
                (it.firstOrNull() as? Stage.Other)?.type == REGISTRATION_SUBSCRIPTION_TYPE
            }
        } else {
            flows.firstOrNull {
                it.firstOrNull { (it as? Stage.Other)?.type == REGISTRATION_SUBSCRIPTION_TYPE } == null
            }
        } ?: throw IllegalArgumentException(context.getString(R.string.wrong_signup_config))
}