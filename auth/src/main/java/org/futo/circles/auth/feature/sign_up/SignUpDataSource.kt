package org.futo.circles.auth.feature.sign_up

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.bsspeke.BSSpekeClientProvider
import org.futo.circles.auth.feature.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton

enum class SignUpNavigationEvents { TokenValidation, Subscription, AcceptTerm, ValidateEmail, Password, BSspeke, Username }

@Singleton
class SignUpDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource,
    private val preferencesProvider: PreferencesProvider
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<SignUpNavigationEvents>()
    val finishRegistrationLiveData = SingleEventLiveData<Response<Unit>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData

    val stagesToComplete = mutableListOf<Stage>()

    var currentStage: Stage? = null
        private set

    var userName: String = ""
        private set
    var domain: String = ""
        private set

    fun startSignUpStages(
        stages: List<Stage>,
        serverDomain: String
    ) {
        currentStage = null
        stagesToComplete.clear()
        domain = serverDomain
        stagesToComplete.addAll(stages)
        navigateToNextStage()
    }

    suspend fun performRegistrationStage(
        authParams: JsonDict,
        name: String? = null
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
            ((result as? RegistrationResult.FlowResponse)?.flowResult?.missingStages?.firstOrNull() as? Stage.Other)?.type
        return nextStageType == (currentStage as? Stage.Other)?.type && nextStageType != null
    }

    private suspend fun finishRegistration(session: Session) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().reset()
        MatrixSessionProvider.awaitForSessionStart(session)
        preferencesProvider.setShouldShowAllExplanations()
        createPassPhraseDataSource.createPassPhraseBackup()
        BSSpekeClientProvider.clear()
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

    companion object {
        const val REGISTRATION_TOKEN_TYPE = "m.login.registration_token"
        const val REGISTRATION_SUBSCRIPTION_TYPE = "org.futo.subscription.google_play"
        const val REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE = "m.enroll.email.request_token"
        const val REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE = "m.enroll.email.submit_token"
        const val REGISTRATION_USERNAME_TYPE = "m.enroll.username"
        const val REGISTRATION_PASSWORD_TYPE = "m.enroll.password"
        const val REGISTRATION_BSSPEKE_OPRF_TYPE = "m.enroll.bsspeke-ecc.oprf"
        const val REGISTRATION_BSSPEKE_SAVE_TYPE = "m.enroll.bsspeke-ecc.save"
    }
}