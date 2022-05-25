package com.futo.circles.feature.settings.deactivate

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DeactivateAccountDataSource(context: Context) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    suspend fun deactivateAccount(password: String): Response<Unit> = createResult {
        session.accountService()
            .deactivateAccount(false, object : UserInteractiveAuthInterceptor {
                override fun performStage(
                    flowResponse: RegistrationFlowResponse,
                    errCode: String?,
                    promise: Continuation<UIABaseAuth>
                ) {
                    errCode?.let { promise.resumeWithException(IllegalArgumentException()) }

                    val stages = flowResponse.flows?.firstOrNull()?.stages?.takeIf { it.size == 1 }

                    if (stages?.firstOrNull() != LoginFlowTypes.PASSWORD)
                        promise.resumeWithException(IllegalArgumentException())

                    promise.resume(
                        UserPasswordAuth(
                            session = flowResponse.session,
                            password = password,
                            user = session.myUserId
                        )
                    )
                }
            })
    }
}