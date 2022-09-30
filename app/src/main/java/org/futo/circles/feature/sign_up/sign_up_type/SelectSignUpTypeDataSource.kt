package org.futo.circles.feature.sign_up.sign_up_type

import android.content.Context
import kotlinx.coroutines.delay
import org.futo.circles.R
import org.futo.circles.core.*
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider
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
        val flows = authService.getRegistrationWizard().getAllRegistrationFlows()
        signUpDataSource.startSignUpStages(
            prepareStagesList(isSubscription, flows),
            name,
            subscriptionReceipt
        )
    }
    
    private fun prepareStagesList(isSubscription: Boolean, flows: List<List<Stage>>): List<Stage> {
        val flow = if (isSubscription) {
            flows.firstOrNull {
                (it.firstOrNull() as? Stage.Other)?.type?.endsWith(
                    REGISTRATION_SUBSCRIPTION_KEY_EXTENSION
                ) == true
            }
        } else {
            flows.firstOrNull {
                (it.firstOrNull() as? Stage.Other)?.type?.endsWith(
                    REGISTRATION_TOKEN_KEY_EXTENSION
                ) == true
            }
        } ?: throw IllegalArgumentException(context.getString(R.string.wrong_signup_config))

        return mapEmailStages(flow)
    }

    private fun mapEmailStages(stages: List<Stage>): List<Stage> {
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