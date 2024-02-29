package org.futo.circles.auth.feature.uia.flow.reauth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.coroutineScope
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.toFlowsWithStages
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

class AuthConfirmationProvider @Inject constructor(
    private val uiaFactory: UIADataSource.Factory
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
            MatrixSessionProvider.getSessionOrThrow().coroutineScope.launch(Dispatchers.IO) {
                UIADataSourceProvider.create(UIAFlowType.ReAuth, uiaFactory)
                    .apply { startUIAStages(stages, flowResponse.session ?: "", promise) }
            }
        } else {
            UIADataSourceProvider.getDataSourceOrThrow()
                .onStageResult(promise, flowResponse, errCode)
        }
    }
}