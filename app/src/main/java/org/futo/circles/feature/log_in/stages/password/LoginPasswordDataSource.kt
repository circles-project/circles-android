package org.futo.circles.feature.log_in.stages.password

import android.content.Context
import org.futo.circles.R
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import org.matrix.android.sdk.api.session.Session

class LoginPasswordDataSource(
    private val context: Context,
    private val loginStagesDataSource: LoginStagesDataSource
) {

    suspend fun logIn(password: String): Response<Session> {
        val result = createResult {
            MatrixInstanceProvider.matrix.authenticationService().getLoginWizard().login(
                login = loginStagesDataSource.userName,
                password = password,
                initialDeviceName = context.getString(
                    R.string.initial_device_name,
                    context.getString(R.string.app_name)
                )
            )
        }
        (result as? Response.Success)?.let {
            loginStagesDataSource.stageCompleted(
                RegistrationResult.Success(it.data),
                password
            )
        }
        return result
    }
}