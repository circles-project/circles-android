package org.futo.circles.auth.feature.reauth

import org.futo.circles.core.SingleEventLiveData
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.toFlowsWithStages
import javax.inject.Inject
import kotlin.coroutines.Continuation

class AuthConfirmationProvider @Inject constructor(
    private val reAuthStagesDataSource: ReAuthStagesDataSource
) : UserInteractiveAuthInterceptor {

    val startReAuthEventLiveData = SingleEventLiveData<Unit>()

    fun getNewChangedPassword() = reAuthStagesDataSource.getNewPassword()
    fun getOldPassword() = reAuthStagesDataSource.getOldPassword()

    override fun performStage(
        flowResponse: RegistrationFlowResponse,
        errCode: String?,
        promise: Continuation<UIABaseAuth>
    ) {
        if (flowResponse.completedStages.isNullOrEmpty()) {
            val stages = flowResponse.toFlowsWithStages().firstOrNull() ?: emptyList()
            startReAuthEventLiveData.postValue(Unit)
            reAuthStagesDataSource.startReAuthStages(flowResponse.session ?: "", stages, promise)
        } else {
            reAuthStagesDataSource.onStageResult(promise, flowResponse, errCode)
        }
    }
}