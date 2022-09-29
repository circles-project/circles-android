package org.futo.circles.feature.sign_up.sign_up_type

import android.content.Context
import org.futo.circles.core.REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE
import org.futo.circles.core.REGISTRATION_EMAIL_STAGE_KEY_PREFIX
import org.futo.circles.core.REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationAvailability
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class SelectSignUpTypeDataSource(
    private val context: Context,
    private val signUpDataSource: SignUpDataSource
) {

    private val authService by lazy { MatrixInstanceProvider.matrix.authenticationService() }

    fun clearSubtitle() {
        signUpDataSource.clearSubtitle()
    }

    suspend fun startNewRegistration(
        name: String,
        domain: String,
        isSubscription: Boolean,
        subscriptionReceipt: String?
    ) = createResult {
        authService.cancelPendingLoginOrRegistration()
        authService.initiateAuth(buildHomeServerConfigFromDomain(domain))
        validateUserName(name)
        (authService.getRegistrationWizard()
            .getRegistrationFlow() as? RegistrationResult.FlowResponse)
            ?.let {
                signUpDataSource.startSignUpStages(
                    prepareStagesList(it.flowResult.missingStages),
                    name,
                    isSubscription,
                    subscriptionReceipt
                )
            }
    }

    private suspend fun validateUserName(name: String) {
        (authService.getRegistrationWizard()
            .registrationAvailable(name) as? RegistrationAvailability.NotAvailable)?.let {
            throw IllegalArgumentException(it.failure.error.message)
        }
    }

    private fun prepareStagesList(stages: List<Stage>): List<Stage> {
        val requestTokenStage =
            stages.firstOrNull { (it as? Stage.Other)?.type == REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE }
        val submitTokenStage =
            stages.firstOrNull { (it as? Stage.Other)?.type == REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE }

        return if (requestTokenStage != null && submitTokenStage != null)
            stages.toMutableList().apply {
                val position = stages.indexOf(requestTokenStage)
                add(position, Stage.Other(true, REGISTRATION_EMAIL_STAGE_KEY_PREFIX, null))
                remove(requestTokenStage)
                remove(submitTokenStage)
            } else stages
    }

}