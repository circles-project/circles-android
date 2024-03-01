package org.futo.circles.auth.feature.uia.stages.validate_email

import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_EMAIL_REQUEST_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_EMAIL_REQUEST_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_EMAIL_SUBMIT_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class ValidateEmailDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun sendValidationCode(
        email: String, subscribeToUpdates: Boolean
    ): Response<RegistrationResult> = uiaDataSource.performUIAStage(
        mapOf(
            TYPE_PARAM_KEY to if (isLogin()) LOGIN_EMAIL_REQUEST_TOKEN_TYPE else ENROLL_EMAIL_REQUEST_TOKEN_TYPE,
            EMAIL_PARAM_KEY to email,
            SUBSCRIBE_TO_LIST to subscribeToUpdates
        )
    )

    suspend fun validateEmail(code: String): Response<RegistrationResult> =
        uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to if (isLogin()) LOGIN_EMAIL_REQUEST_TOKEN_TYPE else ENROLL_EMAIL_SUBMIT_TOKEN_TYPE,
                TOKEN_PARAM_KEY to code
            )
        )

    private fun isLogin(): Boolean {
        val currentStageKey = UIADataSourceProvider.getDataSourceOrThrow().getCurrentStageKey()
        return currentStageKey == LOGIN_EMAIL_REQUEST_TOKEN_TYPE || currentStageKey == LOGIN_EMAIL_SUBMIT_TOKEN_TYPE
    }

    fun getPrefilledEmail(): String? =
        if (isLogin()) ((uiaDataSource.currentStage as? Stage.Other)?.params?.get(EMAILS_LIST_KEY) as? List<*>)?.firstOrNull()
            ?.toString() else null

    fun shouldShowSubscribeToEmail(): Boolean =
        (uiaDataSource.currentStage as? Stage.Other)?.params?.get(OFFER_LIST_SUBSCRIPTION_KEY) as? Boolean
            ?: false

    companion object {
        private const val EMAIL_PARAM_KEY = "email"
        private const val TOKEN_PARAM_KEY = "token"
        private const val SUBSCRIBE_TO_LIST = "subscribe_to_list"
        private const val OFFER_LIST_SUBSCRIPTION_KEY = "offer_list_subscription"
        private const val EMAILS_LIST_KEY = "addresses"
    }
}