package org.matrix.android.sdk.internal.auth.registration

import org.matrix.android.sdk.api.auth.data.Credentials
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.toRegistrationFlowResponse
import org.matrix.android.sdk.internal.auth.AuthAPI
import org.matrix.android.sdk.internal.network.executeRequest
import org.matrix.android.sdk.internal.task.Task

internal interface RegisterOtherTask : Task<RegisterOtherTask.Params, Credentials> {
    data class Params(
        val registrationOtherParams: RegistrationOtherParams
    )
}

internal class DefaultRegisterOtherTask(
    private val authAPI: AuthAPI
) : RegisterOtherTask {

    override suspend fun execute(params: RegisterOtherTask.Params): Credentials {
        try {
            return executeRequest(null) {
                authAPI.registerOther(params.registrationOtherParams)
            }
        } catch (throwable: Throwable) {
            throw throwable.toRegistrationFlowResponse()
                ?.let { Failure.RegistrationFlowError(it) }
                ?: throwable
        }
    }
}