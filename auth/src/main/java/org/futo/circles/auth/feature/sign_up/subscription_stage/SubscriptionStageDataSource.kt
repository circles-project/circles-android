package org.futo.circles.auth.feature.sign_up.subscription_stage

import org.futo.circles.auth.feature.uia.UIADataSource.Companion.REGISTRATION_SUBSCRIPTION_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class SubscriptionStageDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun validateSubscription(
        subscriptionReceiptData: SubscriptionReceiptData
    ): Response<RegistrationResult> =
        uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_SUBSCRIPTION_TYPE,
                ORDER_ID_KEY to subscriptionReceiptData.orderId,
                PACKAGE_KEY to subscriptionReceiptData.packageName,
                SUBSCRIPTION_ID_KEY to subscriptionReceiptData.productId,
                TOKEN_KEY to subscriptionReceiptData.purchaseToken
            )
        )

    fun getProductIdsList() = ((uiaDataSource.currentStage as? Stage.Other)
        ?.params?.get(SUBSCRIPTION_IDS_PARAMS_KEY) as? List<*>)
        ?.map { it.toString() }
        ?: emptyList()

    companion object {
        private const val ORDER_ID_KEY = "order_id"
        private const val PACKAGE_KEY = "package"
        private const val SUBSCRIPTION_ID_KEY = "subscription_id"
        private const val TOKEN_KEY = "token"
        private const val SUBSCRIPTION_IDS_PARAMS_KEY = "product_ids"
    }
}