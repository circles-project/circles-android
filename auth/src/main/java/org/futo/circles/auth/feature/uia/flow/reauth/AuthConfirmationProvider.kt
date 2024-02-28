package org.futo.circles.auth.feature.uia.flow.reauth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.core.base.SingleEventLiveData
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.toFlowsWithStages
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

class AuthConfirmationProvider @Inject constructor(
    uiaFactory: UIADataSource.Factory
) : UserInteractiveAuthInterceptor {

    val startReAuthEventLiveData = SingleEventLiveData<Unit>()
    private val uiaDataSource by lazy {
        UIADataSourceProvider.create(UIAFlowType.ReAuth, uiaFactory)
    }

    override fun performStage(
        flowResponse: RegistrationFlowResponse,
        errCode: String?,
        promise: Continuation<UIABaseAuth>
    ) {
        if (errCode != null) promise.resumeWithException(IllegalStateException(errCode))
        if (flowResponse.completedStages.isNullOrEmpty()) {
            val stages = flowResponse.toFlowsWithStages().firstOrNull() ?: emptyList()
            startReAuthEventLiveData.postValue(Unit)
            MainScope().launch(Dispatchers.IO) {
                uiaDataSource.startUIAStages(stages, flowResponse.session ?: "", promise)
            }
        } else {
            uiaDataSource.onStageResult(promise, flowResponse, errCode)
        }
    }
}