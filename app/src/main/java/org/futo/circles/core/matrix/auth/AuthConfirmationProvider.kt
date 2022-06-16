package org.futo.circles.core.matrix.auth

import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.auth.UserPasswordAuth
import org.matrix.android.sdk.api.auth.data.LoginFlowTypes
import org.matrix.android.sdk.api.auth.registration.RegistrationFlowResponse
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthConfirmationProvider {


    fun getAuthInterceptor(password: String) = object : UserInteractiveAuthInterceptor {
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
                    user = MatrixSessionProvider.currentSession?.myUserId
                )
            )
        }
    }
}