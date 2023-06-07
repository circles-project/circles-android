package org.futo.circles.auth.feature.reauth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.auth.model.CustomUIAuth
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.auth.registration.toFlowResult
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ReAuthStagesDataSource @Inject constructor(
    @ApplicationContext context: Context,
) : BaseLoginStagesDataSource(context) {

    val finishReAuthEventLiveData = org.futo.circles.core.SingleEventLiveData<Unit>()
    private var authPromise: Continuation<UIABaseAuth>? = null
    private var sessionId: String = ""
    private var stageResultContinuation: Continuation<Response<RegistrationResult>>? =
        null
    private val session by lazy {
        MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
    }

    fun startReAuthStages(
        session: String,
        loginStages: List<Stage>,
        promise: Continuation<UIABaseAuth>
    ) {
        sessionId = session
        authPromise = promise
        stageResultContinuation = null
        val userId = MatrixSessionProvider.currentSession?.myUserId ?: ""
        val domain = userId.substringAfter(":")
        val name = userId.substringAfter("@").substringBefore(":")
        super.startLoginStages(loginStages, name, domain)
    }

    override suspend fun performLoginStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> {
        authPromise?.resume(CustomUIAuth(sessionId, authParams))

        val result = if (isLastStage()) Response.Success(RegistrationResult.Success(session))
        else awaitForStageResult()

        (result as? Response.Success)?.let { stageCompleted(it.data, password) }
        return result
    }

    fun onStageResult(
        promise: Continuation<UIABaseAuth>,
        flowResponse: RegistrationFlowResponse,
        errCode: String?
    ) {
        authPromise = promise
        val result = errCode?.let {
            Response.Error(it)
        } ?: Response.Success(RegistrationResult.FlowResponse(flowResponse.toFlowResult()))
        stageResultContinuation?.resume(result)
    }

    private fun stageCompleted(result: RegistrationResult, password: String?) {
        if (isStageRetry(result)) return
        password?.let { userPassword = it }
        (result as? RegistrationResult.Success)?.let {
            finishReAuthEventLiveData.postValue(Unit)
        } ?: navigateToNextStage()
    }

    private suspend fun awaitForStageResult() = suspendCoroutine { stageResultContinuation = it }

    private fun isLastStage() = stagesToComplete.last() == currentStage

    fun getPasswordFormLastAuth() = userPassword
}