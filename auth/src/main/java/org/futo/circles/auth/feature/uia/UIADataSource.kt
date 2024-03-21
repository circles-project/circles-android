package org.futo.circles.auth.feature.uia

import androidx.lifecycle.MutableLiveData
import org.futo.circles.auth.feature.uia.flow.LoginStagesDataSource
import org.futo.circles.auth.feature.uia.flow.SignUpStagesDataSource
import org.futo.circles.auth.feature.uia.flow.reauth.ReAuthStagesDataSource
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.auth.model.UIANavigationEvent
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import kotlin.coroutines.Continuation

abstract class UIADataSource {

    class Factory @Inject constructor(
        private val loginStagesDataSource: LoginStagesDataSource,
        private val reAuthStagesDataSource: ReAuthStagesDataSource,
        private val signUpStagesDataSource: SignUpStagesDataSource
    ) {
        fun create(flowType: UIAFlowType): UIADataSource = when (flowType) {
            UIAFlowType.Login -> loginStagesDataSource
            UIAFlowType.Signup -> signUpStagesDataSource
            UIAFlowType.ReAuth -> reAuthStagesDataSource
            UIAFlowType.ForgotPassword -> loginStagesDataSource
        }
    }


    val subtitleLiveData = MutableLiveData<Pair<Int, Int>>()
    val stagesNavigationLiveData = SingleEventLiveData<UIANavigationEvent>()
    val finishUIAEventLiveData = SingleEventLiveData<Session>()

    val stagesToComplete = mutableListOf<Stage>()
    var currentStage: Stage? = null
        private set
    var userName: String = ""
        protected set
    var domain: String = ""
        private set


    open suspend fun startUIAStages(
        stages: List<Stage>,
        serverDomain: String,
        name: String? = null
    ) {
        currentStage = null
        this.userName = name ?: ""
        stagesToComplete.clear()
        domain = serverDomain
        stagesToComplete.addAll(stages)
        navigateToNextStage()
    }

    open suspend fun startUIAStages(
        stages: List<Stage>,
        session: String,
        promise: Continuation<UIABaseAuth>
    ) {
        throw IllegalArgumentException("Override only for AuthConfirmation provider usage")
    }

    abstract suspend fun performUIAStage(
        authParams: JsonDict,
        name: String? = null,
        password: String? = null
    ): Response<RegistrationResult>

    open fun onStageResult(
        promise: Continuation<UIABaseAuth>,
        flowResponse: RegistrationFlowResponse,
        errCode: String?
    ) {
        throw IllegalArgumentException("Override only for AuthConfirmation provider usage")
    }


    suspend fun stageCompleted(result: RegistrationResult) {
        if (isStageRetry(result)) return
        (result as? RegistrationResult.Success)?.let {
            finishUIAEventLiveData.postValue(it.session)
        } ?: navigateToNextStage()
    }

    fun getCurrentStageKey() = when (currentStage) {
        is Stage.Other -> (currentStage as Stage.Other).type
        else -> LoginFlowTypes.TERMS
    }

    fun getUserId() = "@${userName}:${domain}"

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
            else -> throw IllegalArgumentException("Not supported stage $stage")
        }
        event?.let { stagesNavigationLiveData.postValue(it) }
        updatePageSubtitle()
    }


    private suspend fun handleStageOther(type: String): UIANavigationEvent? = when (type) {
        SUBSCRIPTION_FREE_TYPE -> {
            performUIAStage(mapOf(TYPE_PARAM_KEY to type))
            null
        }

        LOGIN_REGISTRATION_TOKEN_TYPE -> UIANavigationEvent.TokenValidation
        SUBSCRPTION_GOOGLE_TYPE -> UIANavigationEvent.Subscription
        LOGIN_EMAIL_REQUEST_TOKEN_TYPE,
        ENROLL_EMAIL_REQUEST_TOKEN_TYPE -> UIANavigationEvent.ValidateEmail

        LOGIN_EMAIL_SUBMIT_TOKEN_TYPE,
        ENROLL_EMAIL_SUBMIT_TOKEN_TYPE -> null //stay on same screen
        ENROLL_USERNAME_TYPE -> UIANavigationEvent.Username
        ENROLL_PASSWORD_TYPE,
        LOGIN_PASSWORD_TYPE,
        LOGIN_BSSPEKE_OPRF_TYPE,
        DIRECT_LOGIN_PASSWORD_TYPE,
        ENROLL_BSSPEKE_OPRF_TYPE -> UIANavigationEvent.Password

        ENROLL_BSSPEKE_SAVE_TYPE -> null
        LOGIN_BSSPEKE_VERIFY_TYPE -> null
        else -> throw IllegalArgumentException("Not supported stage $type")

    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun updatePageSubtitle() {
        val size = stagesToComplete.size
        val number = getCurrentStageIndex() + 1
        subtitleLiveData.postValue(number to size)
    }

    companion object {
        //params
        const val TYPE_PARAM_KEY = "type"
        const val USER_PARAM_KEY = "user"
        const val LOGIN_PASSWORD_USER_ID_TYPE = "m.id.user"

        //stages password
        const val LOGIN_PASSWORD_TYPE = "m.login.password"
        const val DIRECT_LOGIN_PASSWORD_TYPE = "m.login.password.direct"
        const val LOGIN_BSSPEKE_OPRF_TYPE = "m.login.bsspeke-ecc.oprf"
        const val LOGIN_BSSPEKE_VERIFY_TYPE = "m.login.bsspeke-ecc.verify"
        const val ENROLL_BSSPEKE_OPRF_TYPE = "m.enroll.bsspeke-ecc.oprf"
        const val ENROLL_BSSPEKE_SAVE_TYPE = "m.enroll.bsspeke-ecc.save"

        //stages subscription
        const val SUBSCRIPTION_FREE_TYPE = "org.futo.subscriptions.free_forever"
        const val SUBSCRPTION_GOOGLE_TYPE = "org.futo.subscriptions.google_play"

        //stages email
        const val LOGIN_EMAIL_REQUEST_TOKEN_TYPE = "m.login.email.request_token"
        const val LOGIN_EMAIL_SUBMIT_TOKEN_TYPE = "m.login.email.submit_token"
        const val ENROLL_EMAIL_REQUEST_TOKEN_TYPE = "m.enroll.email.request_token"
        const val ENROLL_EMAIL_SUBMIT_TOKEN_TYPE = "m.enroll.email.submit_token"

        const val LOGIN_REGISTRATION_TOKEN_TYPE = "m.login.registration_token"
        const val ENROLL_USERNAME_TYPE = "m.enroll.username"
        const val ENROLL_PASSWORD_TYPE = "m.enroll.password"

    }
}