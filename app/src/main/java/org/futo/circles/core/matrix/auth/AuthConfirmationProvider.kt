package org.futo.circles.core.matrix.auth

import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.feature.reauth.ReAuthStagesDataSource
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.toFlowsWithStages
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

class AuthConfirmationProvider(
    private val reAuthStagesDataSource: ReAuthStagesDataSource
) : UserInteractiveAuthInterceptor {

    val startReAuthEventLiveData = SingleEventLiveData<Unit>()

    override fun performStage(
        flowResponse: RegistrationFlowResponse,
        errCode: String?,
        promise: Continuation<UIABaseAuth>
    ) {
        errCode?.let {
            promise.resumeWithException(IllegalArgumentException(it))
            return
        }

        if (flowResponse.completedStages.isNullOrEmpty()) {
            val stages = flowResponse.toFlowsWithStages().firstOrNull() ?: emptyList()
            val session = flowResponse.session
            if (session == null) {
                promise.resumeWithException(IllegalArgumentException())
                return
            }
            startReAuthEventLiveData.postValue(Unit)
            reAuthStagesDataSource.startReAuthStages(session, stages, promise)
        } else {
            reAuthStagesDataSource.handleNextStage()
        }
    }
}