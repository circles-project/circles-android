package org.futo.circles.feature.reauth

import android.content.Context
import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.extensions.Response
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.auth.registration.Stage
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.util.JsonDict
import kotlin.coroutines.Continuation

class ReAuthStagesDataSource(
    context: Context,
) : BaseLoginStagesDataSource(context) {

    private var authPromise: Continuation<UIABaseAuth>? = null

    override fun startLoginStages(
        loginStages: List<Stage>,
        name: String,
        serverDomain: String,
        promise: Continuation<UIABaseAuth>?
    ) {
        authPromise = promise
        super.startLoginStages(loginStages, name, serverDomain, promise)
    }

    override suspend fun performLoginStage(
        authParams: JsonDict,
        password: String?
    ): Response<RegistrationResult> {
        TODO("Not yet implemented")
    }

    override suspend fun finishLogin(session: Session, password: String?) {
        TODO("Not yet implemented")
    }

}