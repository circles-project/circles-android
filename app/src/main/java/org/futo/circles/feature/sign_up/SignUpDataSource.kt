package org.futo.circles.feature.sign_up

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.*
import org.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.subscription_stage.SubscriptionStageDataSource
import org.futo.circles.model.SubscriptionReceiptData
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict

enum class SignUpNavigationEvents { TokenValidation, Subscription, AcceptTerm, ValidateEmail, Password, BSspeke, Username }

class SignUpDataSource(
    private val context: Context,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<SignUpNavigationEvents>()
    val finishRegistrationLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData
    val spaceTreeLoadingLiveData = coreSpacesTreeBuilder.loadingLiveData

    val stagesToComplete = mutableListOf<Stage>()

    var currentStage: Stage? = null
        private set

    var userName: String = ""
        private set
    var domain: String = ""
        private set

    private var passphrase: String = ""
    private val subscriptionStageDataSource = SubscriptionStageDataSource(this)

    suspend fun startSignUpStages(
        stages: List<Stage>,
        serverDomain: String,
        subscriptionReceiptData: SubscriptionReceiptData?
    ) {
        currentStage = null
        stagesToComplete.clear()
        passphrase = ""
        domain = serverDomain
        stagesToComplete.addAll(stages)
        subscriptionReceiptData?.let { skipSubscriptionStageIfValid(it) } ?: navigateToNextStage()
    }

    private suspend fun skipSubscriptionStageIfValid(subscriptionReceiptData: SubscriptionReceiptData) {
        setNextStage()
        (currentStage as? Stage.Other)?.takeIf {
            it.type == REGISTRATION_SUBSCRIPTION_TYPE
        } ?: run {
            currentStage = null
            navigateToNextStage()
            return
        }
        val response = subscriptionStageDataSource.validateSubscription(subscriptionReceiptData)

        if (response is Response.Error) {
            currentStage = null
            navigateToNextStage()
        }
    }

    suspend fun performRegistrationStage(
        authParams: JsonDict,
        name: String? = null,
        password: String? = null
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
        val result = createResult {
            wizard.registrationCustom(authParams, context.getString(R.string.initial_device_name))
        }

        (result as? Response.Success)?.let {
            name?.let { userName = it }
            password?.let { passphrase = it }
            stageCompleted(result.data)
        }
        return result
    }

    private suspend fun stageCompleted(result: RegistrationResult?) {
        if (isStageRetry(result)) return
        (result as? RegistrationResult.Success)?.let {
            finishRegistrationLiveData.postValue(finishRegistration(it.session))
        } ?: navigateToNextStage()
    }

    fun clearSubtitle() {
        subtitleLiveData.postValue("")
    }

    private fun isStageRetry(result: RegistrationResult?): Boolean {
        val nextStageType =
            ((result as? RegistrationResult.FlowResponse)?.flowResult?.missingStages?.lastOrNull() as? Stage.Other)?.type
        return nextStageType == (currentStage as? Stage.Other)?.type && nextStageType != null
    }

    private suspend fun finishRegistration(session: Session) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().reset()
        MatrixSessionProvider.awaitForSessionStart(session)
        createPassPhraseDataSource.createPassPhraseBackup(userName, passphrase)
        coreSpacesTreeBuilder.createCoreSpacesTree()
    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun setNextStage() {
        currentStage = currentStage?.let {
            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
        } ?: stagesToComplete.firstOrNull()
    }

    private fun navigateToNextStage() {
        setNextStage()
        val event = when (val stage = currentStage) {
            is Stage.Terms -> SignUpNavigationEvents.AcceptTerm
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }
        event?.let { navigationLiveData.postValue(it) }
        updatePageSubtitle()
    }

    private fun handleStageOther(type: String): SignUpNavigationEvents? = when (type) {
        REGISTRATION_TOKEN_TYPE -> SignUpNavigationEvents.TokenValidation
        REGISTRATION_SUBSCRIPTION_TYPE -> SignUpNavigationEvents.Subscription
        REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE -> SignUpNavigationEvents.ValidateEmail
        REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE -> null
        REGISTRATION_USERNAME_TYPE -> SignUpNavigationEvents.Username
        REGISTRATION_PASSWORD_TYPE -> SignUpNavigationEvents.Password
        REGISTRATION_BSSPEKE_OPRF_TYPE -> SignUpNavigationEvents.BSspeke
        REGISTRATION_BSSPEKE_SAVE_TYPE -> null
        else -> throw IllegalArgumentException(
            context.getString(R.string.not_supported_stage_format, type)
        )
    }

    private fun updatePageSubtitle() {
        val size = stagesToComplete.size
        val number = getCurrentStageIndex() + 1
        val subtitle = context.getString(R.string.sign_up_stage_subtitle_format, number, size)
        subtitleLiveData.postValue(subtitle)
    }
}