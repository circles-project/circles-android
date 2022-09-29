package org.futo.circles.feature.sign_up

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.futo.circles.R
import org.futo.circles.core.*
import org.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.sign_up.subscription_stage.SubscriptionStageDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session

enum class SignUpNavigationEvents { TokenValidation, Subscription, AcceptTerm, ValidateEmail, Password }

class SignUpDataSource(
    private val context: Context,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<SignUpNavigationEvents>()
    val finishRegistrationLiveData = SingleEventLiveData<Response<List<Unit>>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData

    private val stagesToComplete = mutableListOf<Stage>()

    var currentStage: Stage? = null
        private set

    var currentHomeServerUrl: String = ""
        private set

    private var passphrase: String = ""
    private var userName: String = ""

    suspend fun startSignUpStages(
        stages: List<Stage>,
        name: String,
        homeServerUrl: String,
        isSubscription: Boolean,
        subscriptionReceipt: String?
    ) {
        currentStage = null
        stagesToComplete.clear()
        passphrase = ""
        userName = name
        currentHomeServerUrl = homeServerUrl

        setupStages(stages, isSubscription)

        if (isSubscription && subscriptionReceipt != null) {
            val response = SubscriptionStageDataSource(this)
                .validateSubscriptionReceipt(subscriptionReceipt)
            if (response is Response.Error) navigateToNextStage()
        } else {
            navigateToNextStage()
        }
    }

    suspend fun stageCompleted(result: RegistrationResult?) {
        (result as? RegistrationResult.Success)?.let {
            finishRegistrationLiveData.postValue(finishRegistration(it.session))
        } ?: navigateToNextStage()
    }

    fun clearSubtitle() {
        subtitleLiveData.postValue("")
    }

    fun setPassword(password: String) {
        passphrase = password
    }

    private fun setupStages(stages: List<Stage>, isSubscription: Boolean) {
        val otherStages = stages.filterIsInstance<Stage.Other>()
        val firstStage = otherStages.firstOrNull {
            if (isSubscription) it.type.endsWith(REGISTRATION_SUBSCRIPTION_KEY_EXTENSION)
            else it.type.endsWith(REGISTRATION_TOKEN_KEY_EXTENSION)
        } ?: throw IllegalArgumentException(context.getString(R.string.wrong_signup_config))
        stagesToComplete.add(firstStage)
        stagesToComplete.addAll(stages.filter { it.mandatory && (it as? Stage.Other)?.type != firstStage.type })
    }

    private suspend fun finishRegistration(session: Session) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().reset()
        MatrixSessionProvider.awaitForSessionStart(session)
        coroutineScope {
            listOf(
                async { coreSpacesTreeBuilder.createCoreSpacesTree() },
                async { createPassPhraseDataSource.createPassPhraseBackup(userName, passphrase) }
            ).awaitAll()
        }
    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun navigateToNextStage() {
        val stage = currentStage?.let {
            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
        } ?: stagesToComplete.firstOrNull()

        currentStage = stage

        val event = when (stage) {
            is Stage.Terms -> SignUpNavigationEvents.AcceptTerm
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }

        navigationLiveData.postValue(event)

        updatePageSubtitle()
    }

    private fun handleStageOther(type: String): SignUpNavigationEvents = when {
        type.endsWith(REGISTRATION_TOKEN_KEY_EXTENSION) -> SignUpNavigationEvents.TokenValidation
        type.endsWith(REGISTRATION_SUBSCRIPTION_KEY_EXTENSION) -> SignUpNavigationEvents.Subscription
        type.startsWith(REGISTRATION_EMAIL_STAGE_KEY_PREFIX) -> SignUpNavigationEvents.ValidateEmail
        type.startsWith(REGISTRATION_PASSWORD_TYPE) -> SignUpNavigationEvents.Password
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

    companion object {
        const val TYPE_PARAM_KEY = "type"
    }
}