package org.futo.circles.feature.log_in.stages.password

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class DirectLoginPasswordDataSource(
    private val context: Context,
    private val loginStagesDataSource: BaseLoginStagesDataSource
) : PasswordDataSource {

    override fun getMinimumPasswordLength(): Int = 1

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val result = createResult {
            MatrixInstanceProvider.matrix.authenticationService().getLoginWizard().login(
                login = "@${loginStagesDataSource.userName}:${loginStagesDataSource.domain}",
                password = password,
                initialDeviceName = context.getString(
                    R.string.initial_device_name,
                    context.getString(R.string.app_name)
                )
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