package org.futo.circles.auth.feature.uia

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.base.UIANavigationEvent
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UIADataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<UIANavigationEvent>()

    val stagesToComplete = mutableListOf<Stage>()
    var currentStage: Stage? = null
        private set
    var userName: String = ""
        protected set
    var domain: String = ""
        private set

    var userPassword: String = ""

    abstract suspend fun sendDataForStageResult(authParams: JsonDict): Response<RegistrationResult>

    abstract suspend fun finishStages(session: Session)

    suspend fun startUIAStages(
        stages: List<Stage>,
        serverDomain: String
    ) {
        domain = serverDomain
        userPassword = ""
        currentStage = null
        stagesToComplete.clear()
        stagesToComplete.addAll(stages)
        navigateToNextStage()
    }

    suspend fun performUIAStage(
        authParams: JsonDict,
        name: String? = null,
        password: String? = null
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
        val result = createResult {
            wizard.registrationCustom(
                authParams,
                context.getString(R.string.initial_device_name),
                true
            )
        }

        (result as? Response.Success)?.let {
            name?.let { userName = it }
            password?.let { userPassword = it }
            stageCompleted(result.data)
        }
        return result
    }


    suspend fun stageCompleted(result: RegistrationResult) {
        if (isStageRetry(result)) return
        (result as? RegistrationResult.Success)?.let {
            finishStages(it.session)
        } ?: navigateToNextStage()
    }

    protected fun getIdentifier() = mapOf(
        USER_PARAM_KEY to "@$userName:$domain",
        TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
    )

    private fun isStageRetry(result: RegistrationResult?): Boolean {
        val nextStageType =
            ((result as? RegistrationResult.FlowResponse)?.flowResult?.missingStages?.firstOrNull() as? Stage.Other)?.type
        return nextStageType == (currentStage as? Stage.Other)?.type && nextStageType != null
    }

    private fun setNextStage() {
        currentStage = currentStage?.let {
            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
        } ?: stagesToComplete.firstOrNull()
    }

    private suspend fun navigateToNextStage() {
        setNextStage()
        val event = when (val stage = currentStage) {
            is Stage.Terms -> UIANavigationEvent.AcceptTerm
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }
        event?.let { navigationLiveData.postValue(it) }
        updatePageSubtitle()
    }


    private suspend fun handleStageOther(type: String): UIANavigationEvent? = when (type) {
        REGISTRATION_FREE_TYPE -> {
            performUIAStage(mapOf(TYPE_PARAM_KEY to type))
            null
        }

        REGISTRATION_TOKEN_TYPE -> UIANavigationEvent.TokenValidation
        REGISTRATION_SUBSCRIPTION_TYPE -> UIANavigationEvent.Subscription
        REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE -> UIANavigationEvent.ValidateEmail
        REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE -> null //stay on same screen
        REGISTRATION_USERNAME_TYPE -> UIANavigationEvent.Username
        REGISTRATION_PASSWORD_TYPE -> UIANavigationEvent.Password
        REGISTRATION_BSSPEKE_OPRF_TYPE -> UIANavigationEvent.BSspeke
        REGISTRATION_BSSPEKE_SAVE_TYPE -> null
        LOGIN_PASSWORD_TYPE -> UIANavigationEvent.Password
        DIRECT_LOGIN_PASSWORD_TYPE -> UIANavigationEvent.DirectPassword
        LOGIN_BSSPEKE_OPRF_TYPE -> UIANavigationEvent.BSspekeLogin
        LOGIN_BSSPEKE_VERIFY_TYPE -> null
        else -> throw IllegalArgumentException(
            context.getString(R.string.not_supported_stage_format, type)
        )
    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun updatePageSubtitle() {
        val size = stagesToComplete.size
        val number = getCurrentStageIndex() + 1
        val subtitle = context.getString(R.string.sign_up_stage_subtitle_format, number, size)
        subtitleLiveData.postValue(subtitle)
    }

    companion object {
        //params
        const val USER_PARAM_KEY = "user"
        const val TYPE_PARAM_KEY = "type"
        const val LOGIN_PASSWORD_USER_ID_TYPE = "m.id.user"

        //login stages
        const val LOGIN_PASSWORD_TYPE = "m.login.password"
        const val DIRECT_LOGIN_PASSWORD_TYPE = "m.login.password.direct"
        const val LOGIN_BSSPEKE_OPRF_TYPE = "m.login.bsspeke-ecc.oprf"
        const val LOGIN_BSSPEKE_VERIFY_TYPE = "m.login.bsspeke-ecc.verify"

        //signup stages
        const val REGISTRATION_FREE_TYPE = "org.futo.subscriptions.free_forever"
        const val REGISTRATION_TOKEN_TYPE = "m.login.registration_token"
        const val REGISTRATION_SUBSCRIPTION_TYPE = "org.futo.subscriptions.google_play"
        const val REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE = "m.enroll.email.request_token"
        const val REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE = "m.enroll.email.submit_token"
        const val REGISTRATION_USERNAME_TYPE = "m.enroll.username"
        const val REGISTRATION_PASSWORD_TYPE = "m.enroll.password"
        const val REGISTRATION_BSSPEKE_OPRF_TYPE = "m.enroll.bsspeke-ecc.oprf"
        const val REGISTRATION_BSSPEKE_SAVE_TYPE = "m.enroll.bsspeke-ecc.save"
    }
}