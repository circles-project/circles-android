package com.futo.circles.feature.sign_up.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.core.REGISTRATION_TOKEN_KEY
import com.futo.circles.core.SingleEventLiveData
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session

enum class ExtraSignUpStages { Avatar, Circles }

enum class NavigationEvents { TokenValidation, AcceptTerm, VerifyEmail, SetupAvatar, SetupCircles, FinishSignUp }

class SignUpDataSource(
    private val context: Context
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<NavigationEvents>()

    private val stagesToComplete = mutableListOf<Stage>()

    var currentStage: Stage? = null
        private set


    fun startSignUpStages(stages: List<Stage>) {
        currentStage = null
        stagesToComplete.clear()

        stagesToComplete.addAll(stages)
        ExtraSignUpStages.values().forEach {
            stagesToComplete.add(Stage.Other(false, it.name, null))
        }
        navigateToNextStage()
    }

    fun stageCompleted(result: RegistrationResult?) {
        (result as? RegistrationResult.Success)?.let {
            finishRegistration(it.session)
        } ?: navigateToNextStage()
    }

    fun clearSubtitle() {
        subtitleLiveData.postValue("")
    }

    private fun finishRegistration(session: Session) {
        navigationLiveData.postValue(NavigationEvents.FinishSignUp)
    }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun navigateToNextStage() {
        val stage = currentStage?.let {
            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
        } ?: stagesToComplete.firstOrNull()

        currentStage = stage

        val event = when (stage) {
            is Stage.Email -> NavigationEvents.VerifyEmail
            is Stage.Terms -> NavigationEvents.AcceptTerm
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException("Not supported stage $stage")
        }

        navigationLiveData.postValue(event)

        updatePageSubtitle()
    }

    private fun handleStageOther(type: String): NavigationEvents =
        when (type) {
            REGISTRATION_TOKEN_KEY -> NavigationEvents.TokenValidation
            ExtraSignUpStages.Avatar.name -> NavigationEvents.SetupAvatar
            ExtraSignUpStages.Circles.name -> NavigationEvents.SetupCircles
            else -> throw IllegalArgumentException("Not supported stage $type")
        }

    private fun updatePageSubtitle() {
        val size = stagesToComplete.size
        val number = getCurrentStageIndex() + 1
        val subtitle = context.getString(R.string.sign_up_stage_subtitle_format, number, size)
        subtitleLiveData.postValue(subtitle)
    }


}