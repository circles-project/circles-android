package org.futo.circles.auth.feature.sign_up.uia

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.model.UIANavigationEvent
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
class SignupUIADataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val stagesNavigationLiveData = SingleEventLiveData<UIANavigationEvent>()
    val finishUIAEventLiveData = SingleEventLiveData<Session>()

    val stagesToComplete = mutableListOf<Stage>()
    var currentStage: Stage? = null
        private set
    var homeServerUrl: String = ""
        private set


    fun startSignupUIAStages(
        homeServerUrl: String,
        stages: List<Stage>
    ) {
        this.homeServerUrl = homeServerUrl
        currentStage = null
        stagesToComplete.clear()
        stagesToComplete.addAll(stages)
        navigateToNextStage()
    }

    suspend fun performUIAStage(
        authParams: JsonDict
    ): Response<RegistrationResult> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
        val result = createResult {
            wizard.registrationCustom(authParams, context.getString(R.string.initial_device_name))
        }

        (result as? Response.Success)?.let { stageCompleted(result.data) }
        return result
    }


    private fun stageCompleted(result: RegistrationResult) {
        if (isStageRetry(result)) return
        (result as? RegistrationResult.Success)?.let {
            finishUIAEventLiveData.postValue(it.session)
        } ?: navigateToNextStage()
    }

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

    private fun navigateToNextStage() {
        setNextStage()
        val event = when (val stage = currentStage) {
            is Stage.Terms -> UIANavigationEvent.AcceptTerm
            is Stage.ReCaptcha -> UIANavigationEvent.Recaptcha
            is Stage.Email -> UIANavigationEvent.ValidateEmail
            else -> throw IllegalArgumentException("Not supported stage $stage")
        }
        event.let { stagesNavigationLiveData.postValue(it) }
    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0



}