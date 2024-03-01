package org.futo.circles.auth.feature.uia.flow.reauth

import org.futo.circles.auth.feature.uia.UIADataSource
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
class ReAuthStagesDataSource @Inject constructor() : UIADataSource() {

    private var authPromise: Continuation<UIABaseAuth>? = null
    private var sessionId: String = ""
    private var stageResultContinuation: Continuation<Response<RegistrationResult>>? = null

    override suspend fun startUIAStages(
        stages: List<Stage>,
        session: String,
        promise: Continuation<UIABaseAuth>
    ) {
        sessionId = session
        authPromise = promise
        stageResultContinuation = null
        val userId = MatrixSessionProvider.getSessionOrThrow().myUserId
        val domain = userId.substringAfter(":")
        val name = userId.substringAfter("@").substringBefore(":")
        startUIAStages(stages, domain, name)
    }


    override suspend fun performUIAStage(
        authParams: JsonDict,
        name: String?,
        password: String?
    ): Response<RegistrationResult> {
        authPromise?.resume(CustomUIAuth(sessionId, authParams))

        val result = if (isLastStage())
            Response.Success(RegistrationResult.Success(MatrixSessionProvider.getSessionOrThrow()))
        else awaitForStageResult()

        (result as? Response.Success)?.let {
            stageCompleted(it.data)
        } ?: run { finishUIAEventLiveData.postValue(MatrixSessionProvider.getSessionOrThrow()) }
        return result
    }

    override fun onStageResult(
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

    private suspend fun awaitForStageResult() = suspendCoroutine { stageResultContinuation = it }

    private fun isLastStage() = stagesToComplete.last() == currentStage

}