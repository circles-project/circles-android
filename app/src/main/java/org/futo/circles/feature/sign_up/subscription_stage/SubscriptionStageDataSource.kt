package org.futo.circles.feature.sign_up.subscription_stage

import org.futo.circles.core.REGISTRATION_SUBSCRIPTION_TYPE
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.feature.sign_up.SignUpDataSource.Companion.TYPE_PARAM_KEY
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage

class SubscriptionStageDataSource(
    private val signUpDataSource: SignUpDataSource
) {

    suspend fun validateSubscriptionReceipt(receipt: String): Response<RegistrationResult> =
        signUpDataSource.performRegistrationStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_SUBSCRIPTION_TYPE,
                PRODUCT_PARAM_KEY to receipt
            )
        )

    fun getProductIdsList() = ((signUpDataSource.currentStage as? Stage.Other)
        ?.params?.get(PRODUCT_IDS_KEY) as? List<*>)
        ?.map { it.toString() }
        ?: emptyList()

    companion object {
        private const val PRODUCT_PARAM_KEY = "product"
        private const val PRODUCT_IDS_KEY = "productIds"
    }
}