package org.futo.circles.auth.feature.reauth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.model.CustomUIAuth
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.auth.registration.toFlowResult
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ReAuthStagesDataSource @Inject constructor(
    @ApplicationContext context: Context,
) : UIADataSource(context) {

    private var authPromise: Continuation<UIABaseAuth>? = null
    private var sessionId: String = ""
    private var stageResultContinuation: Continuation<Response<RegistrationResult>>? = null

    suspend fun startReAuthStages(
        session: String,
        loginStages: List<Stage>,
        promise: Continuation<UIABaseAuth>
    ) {
        sessionId = session
        authPromise = promise
        stageResultContinuation = null
        val userId = MatrixSessionProvider.getSessionOrThrow().myUserId
        val domain = userId.substringAfter(":")
        val name = userId.substringAfter("@").substringBefore(":")
        startUIAStages(loginStages, domain)
    }

    override suspend fun sendDataForStageResult(authParams: JsonDict): Response<RegistrationResult> {
        authPromise?.resume(CustomUIAuth(sessionId, authParams))
        return if (isLastStage())
            Response.Success(RegistrationResult.Success(MatrixSessionProvider.getSessionOrThrow()))
        else awaitForStageResult()
    }

    override suspend fun finishStages(session: Session) {
        TODO("Not yet implemented")
    }

//    override suspend fun performLoginStage(
//        authParams: JsonDict,
//        password: String?
//    ): Response<RegistrationResult> {
//        authPromise?.resume(CustomUIAuth(sessionId, authParams))
//
//        val result = if (isLastStage())
//            Response.Success(RegistrationResult.Success(MatrixSessionProvider.getSessionOrThrow()))
//        else awaitForStageResult()
//
//        (result as? Response.Success)?.let {
//            password?.let { userPassword = it }
//            stageCompleted(it.data)
//        } ?: run { finishReAuthEventLiveData.postValue(Unit) }
//        return result
//    }

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

    private suspend fun awaitForStageResult() = suspendCoroutine { stageResultContinuation = it }

    private fun isLastStage() = stagesToComplete.last() == currentStage
}