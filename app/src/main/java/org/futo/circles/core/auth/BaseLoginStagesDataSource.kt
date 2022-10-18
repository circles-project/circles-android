package org.futo.circles.core.auth

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.*
import org.futo.circles.extensions.Response
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import kotlin.coroutines.Continuation

enum class LoginNavigationEvent { Main, SetupCircles, PassPhrase, Password, Terms, DirectPassword, BSspeke }

abstract class BaseLoginStagesDataSource(
    private val context: Context
) {

    val subtitleLiveData = MutableLiveData<String>()
    val loginNavigationLiveData = SingleEventLiveData<LoginNavigationEvent>()

    private val stagesToComplete = mutableListOf<Stage>()
    var currentStage: Stage? = null
        private set

    var userName: String = ""
        private set
    var domain: String = ""
        private set

    protected val initialDisplayName by lazy {
        context.getString(R.string.initial_device_name, context.getString(R.string.app_name))
    }

    open fun startLoginStages(
        loginStages: List<Stage>,
        name: String,
        serverDomain: String,
        promise: Continuation<UIABaseAuth>? = null
    ) {
        userName = name
        domain = serverDomain
        currentStage = null
        stagesToComplete.clear()
        stagesToComplete.addAll(loginStages)
        navigateToNextStage()
    }

    protected fun getIdentifier() = mapOf(
        USER_PARAM_KEY to "@$userName:$domain",
        TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
    )

    abstract suspend fun performLoginStage(
        authParams: JsonDict,
        password: String? = null
    ): Response<RegistrationResult>

    suspend fun stageCompleted(result: RegistrationResult, password: String?) {
        (result as? RegistrationResult.Success)?.let {
            finishLogin(it.session, password)
        } ?: navigateToNextStage()
    }

    abstract suspend fun finishLogin(session: Session, password: String?)

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
            is Stage.Terms -> LoginNavigationEvent.Terms
            is Stage.Other -> handleStageOther(stage.type)
            else -> throw IllegalArgumentException(
                context.getString(R.string.not_supported_stage_format, stage.toString())
            )
        }
        event?.let { loginNavigationLiveData.postValue(it) }
        updatePageSubtitle()
    }

    private fun handleStageOther(type: String): LoginNavigationEvent? = when (type) {
        LOGIN_PASSWORD_TYPE -> LoginNavigationEvent.Password
        DIRECT_LOGIN_PASSWORD_TYPE -> LoginNavigationEvent.DirectPassword
        LOGIN_BSSPEKE_OPRF_TYPE -> LoginNavigationEvent.BSspeke
        LOGIN_BSSPEKE_VERIFY_TYPE -> null
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
        const val USER_PARAM_KEY = "user"
    }
}