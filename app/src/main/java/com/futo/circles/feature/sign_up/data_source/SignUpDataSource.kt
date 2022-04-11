package com.futo.circles.feature.sign_up.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.core.REGISTRATION_TOKEN_KEY
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import com.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixInstanceProvider
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session

enum class NavigationEvents { TokenValidation, AcceptTerm, ValidateEmail }

class SignUpDataSource(
    private val context: Context,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder,
    private val createPassPhraseDataSource: CreatePassPhraseDataSource
) {

    val subtitleLiveData = MutableLiveData<String>()
    val navigationLiveData = SingleEventLiveData<NavigationEvents>()
    val finishRegistrationLiveData = SingleEventLiveData<Response<List<Unit>>>()
    val passPhraseLoadingLiveData = createPassPhraseDataSource.loadingLiveData

    private val stagesToComplete = mutableListOf<Stage>()

    var currentStage: Stage? = null
        private set

    private var passphrase: String = ""

    fun startSignUpStages(stages: List<Stage>, password: String) {
        currentStage = null
        stagesToComplete.clear()
        passphrase = password

        stagesToComplete.addAll(stages.filter { it.mandatory })
        navigateToNextStage()
    }

    suspend fun stageCompleted(result: RegistrationResult?) {
        (result as? RegistrationResult.Success)?.let {
            finishRegistrationLiveData.postValue(finishRegistration(it.session))
        } ?: navigateToNextStage()
    }

    fun clearSubtitle() {
        subtitleLiveData.postValue("")
    }

    private suspend fun finishRegistration(session: Session) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().reset()
        MatrixSessionProvider.awaitForSessionStart(session)
        coroutineScope {
            listOf(
                async { coreSpacesTreeBuilder.createCoreSpacesTree() },
                async { createPassPhraseDataSource.createPassPhraseBackup(passphrase) }
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
            is Stage.Email -> NavigationEvents.ValidateEmail
            is Stage.Terms -> NavigationEvents.AcceptTerm
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }

        navigationLiveData.postValue(event)

        updatePageSubtitle()
    }

    private fun handleStageOther(type: String): NavigationEvents =
        when (type) {
            REGISTRATION_TOKEN_KEY -> NavigationEvents.TokenValidation
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