package org.futo.circles.feature.reauth

import android.content.Context
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.model.CustomUIAuth
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.registration.FlowResult
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.util.JsonDict
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class ReAuthStagesDataSource(
    context: Context,
) : BaseLoginStagesDataSource(context) {

    val finishReAuthEventLiveData = SingleEventLiveData<Unit>()
    private var authPromise: Continuation<UIABaseAuth>? = null
    private var sessionId: String = ""

    fun startReAuthStages(
        session: String,
        loginStages: List<Stage>,
        promise: Continuation<UIABaseAuth>?
    ) {
        sessionId = session
        authPromise = promise
        val userId = MatrixSessionProvider.currentSession?.myUserId ?: ""
        val domain = userId.substringAfter(":")
        val name = userId.substringAfter("@").substringAfter(":")
        super.startLoginStages(loginStages, name, domain)
    }

    override suspend fun performLoginStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> {
        authPromise?.resume(CustomUIAuth(sessionId, authParams))
        return Response.Success(
            RegistrationResult.FlowResponse(
                FlowResult(emptyList(), emptyList())
            )
        )
    }

    fun handleNextStage() {
        if (stagesToComplete.isEmpty()) finishReAuthEventLiveData.postValue(Unit)
        else navigateToNextStage()
    }
}