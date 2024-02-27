package org.futo.circles.auth.feature.sign_up.validate_email

import org.futo.circles.auth.feature.uia.UIADataSource.Companion.REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class ValidateEmailDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun sendValidationCode(
        email: String,
        subscribeToUpdates: Boolean
    ): Response<RegistrationResult> = uiaDataSource.performUIAStage(
        mapOf(
            TYPE_PARAM_KEY to REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE,
            EMAIL_PARAM_KEY to email,
            SUBSCRIBE_TO_LIST to subscribeToUpdates
        )
    )

    suspend fun validateEmail(code: String): Response<RegistrationResult> =
        uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE,
                TOKEN_PARAM_KEY to code
            )
        )

    fun shouldShowSubscribeToEmail(): Boolean =
        (uiaDataSource.currentStage as? Stage.Other)?.params?.get(OFFER_LIST_SUBSCRIPTION_KEY) as? Boolean
            ?: false

    companion object {
        private const val EMAIL_PARAM_KEY = "email"
        private const val TOKEN_PARAM_KEY = "token"
        private const val SUBSCRIBE_TO_LIST = "subscribe_to_list"
        private const val OFFER_LIST_SUBSCRIPTION_KEY = "offer_list_subscription"
    }
}