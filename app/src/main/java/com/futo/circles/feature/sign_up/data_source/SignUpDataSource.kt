package com.futo.circles.feature.sign_up.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import com.futo.circles.core.REGISTRATION_TOKEN_KEY
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.provider.MatrixInstanceProvider
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session

enum class NavigationEvents { TokenValidation, AcceptTerm, ValidateEmail, SetupAvatar, SetupCircles, FinishSignUp }

class SignUpDataSource(
    private val context: Context,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<NavigationEvents>()

    private val stagesToComplete = mutableListOf<Stage>()

    var currentStage: Stage? = null
        private set


    fun startSignUpStages(stages: List<Stage>) {
        currentStage = null
        stagesToComplete.clear()

        stagesToComplete.addAll(stages.filter { it.mandatory })
        navigateToNextStage()
    }

    suspend fun stageCompleted(result: RegistrationResult?) {
        (result as? RegistrationResult.Success)?.let {
            finishRegistration(it.session)
        } ?: navigateToNextStage()
    }

    fun clearSubtitle() {
        subtitleLiveData.postValue("")
    }

    private suspend fun finishRegistration(session: Session) {
        MatrixInstanceProvider.matrix.authenticationService().reset()
        awaitForSessionStart(session)
        coreSpacesTreeBuilder.createCoreSpacesTree()
        navigationLiveData.postValue(NavigationEvents.FinishSignUp)
    }

    private suspend fun awaitForSessionStart(session: Session) =
        suspendCancellableCoroutine<Session> {
            MatrixSessionProvider.startSession(session, object : Session.Listener {
                override fun onSessionStarted(session: Session) {
                    super.onSessionStarted(session)
                    it.resume(session) { session.removeListener(this) }
                }
            })
        }

    private fun getCurrentStageIndex() =
        stagesToComplete.indexOf(currentStage).takeIf { it != -1 } ?: 0

    private fun navigateToNextStage() {
        val stage = currentStage?.let {
            stagesToComplete.getOrNull(getCurrentStageIndex() + 1)
        } ?: stagesToComplete.firstOrNull()

        currentStage = stage

        val event = when (stage) {
            is Stage.Email -> NavigationEvents.ValidateEmail
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
            else -> throw IllegalArgumentException("Not supported stage $type")
        }

    private fun updatePageSubtitle() {
        val size = stagesToComplete.size
        val number = getCurrentStageIndex() + 1
        val subtitle = context.getString(R.string.sign_up_stage_subtitle_format, number, size)
        subtitleLiveData.postValue(subtitle)
    }


}