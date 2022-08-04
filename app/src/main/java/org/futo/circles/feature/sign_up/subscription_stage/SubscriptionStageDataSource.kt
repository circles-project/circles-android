package org.futo.circles.feature.sign_up.subscription_stage

import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class SubscriptionStageDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    private val wizard by lazy {
        MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
    }

    suspend fun validateSubscriptionReceipt(receipt: String): Response<RegistrationResult> {
        val type = (signUpDataSource.currentStage as? Stage.Other)?.type ?: ""

        val result = createResult {
            wizard.registrationCustom(
                mapOf(
                    "type" to type,
                    "product" to receipt
                )
            )
        }

        (result as? Response.Success)?.let { signUpDataSource.stageCompleted(result.data) }

        return result
    }
}