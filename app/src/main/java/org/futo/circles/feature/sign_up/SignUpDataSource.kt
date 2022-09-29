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

    private var passphrase: String = ""
    private var userName: String = ""

    suspend fun startSignUpStages(
        stages: List<Stage>,
        name: String,
        subscriptionReceipt: String?
    ) {
        currentStage = null
        stagesToComplete.clear()
        passphrase = ""
        userName = name
        stagesToComplete.addAll(stages)
        subscriptionReceipt?.let { skipSubscriptionStageIfValid(it) } ?: navigateToNextStage()
    }

    private suspend fun skipSubscriptionStageIfValid(subscriptionReceipt: String) {
        setNextStage()
        (currentStage as? Stage.Other)?.takeIf {
            it.type.endsWith(REGISTRATION_SUBSCRIPTION_KEY_EXTENSION)
        } ?: run {
            currentStage = null
            navigateToNextStage()
            return
        }
        val response = SubscriptionStageDataSource(this)
            .validateSubscriptionReceipt(subscriptionReceipt)

        if (response is Response.Error) {
            currentStage = null
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