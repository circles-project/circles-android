package org.futo.circles.auth.feature.log_in.stages.password

import android.content.Context
import org.futo.circles.auth.R
import org.futo.circles.auth.base.PasswordDataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.auth.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class DirectLoginPasswordDataSource(
    private val context: Context,
    private val loginStagesDataSource: LoginStagesDataSource
) : PasswordDataSource {

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val result = createResult {
            MatrixInstanceProvider.matrix.authenticationService().getLoginWizard().login(
                login = "@${loginStagesDataSource.userName}:${loginStagesDataSource.domain}",
                password = password,
                initialDeviceName = context.getString(R.string.initial_device_name)
            )
        }
        return when (result) {
            is Response.Success -> {
                loginStagesDataSource.stageCompleted(
                    RegistrationResult.Success(result.data), password
                )
                Response.Success(Unit)
            }

            is Response.Error -> result
        }
    }
}