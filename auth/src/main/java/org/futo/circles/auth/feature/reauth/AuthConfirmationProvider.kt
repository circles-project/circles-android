package org.futo.circles.auth.feature.reauth

import org.futo.circles.core.base.SingleEventLiveData
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.toFlowsWithStages
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

class AuthConfirmationProvider @Inject constructor(
    private val reAuthStagesDataSource: ReAuthStagesDataSource
) : UserInteractiveAuthInterceptor {

    val startReAuthEventLiveData = SingleEventLiveData<Unit>()

    override fun performStage(
        flowResponse: RegistrationFlowResponse,
        errCode: String?,
        promise: Continuation<UIABaseAuth>
    ) {
        if (errCode != null) promise.resumeWithException(IllegalStateException(errCode))
        if (flowResponse.completedStages.isNullOrEmpty()) {
            val stages = flowResponse.toFlowsWithStages().firstOrNull() ?: emptyList()
            startReAuthEventLiveData.postValue(Unit)
            reAuthStagesDataSource.startReAuthStages(flowResponse.session ?: "", stages, promise)
        } else {
            reAuthStagesDataSource.onStageResult(promise, flowResponse, errCode)
        }
    }
}