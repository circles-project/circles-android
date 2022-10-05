package org.futo.circles.feature.log_in.stages.password

import org.futo.circles.core.LOGIN_PASSWORD_TYPE
import org.futo.circles.core.LOGIN_PASSWORD_USER_ID_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.futo.circles.provider.MatrixInstanceProvider
import org.matrix.android.sdk.api.auth.registration.Stage

class LoginPasswordDataSource(
    private val loginStagesDataSource: LoginStagesDataSource
) : PasswordDataSource {

    override fun getMinimumPasswordLength(): Int =
        ((loginStagesDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            MINIMUM_LENGTH_KEY, 1.0
        ) as? Double)?.toInt() ?: 1


    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val wizard = MatrixInstanceProvider.matrix.authenticationService().getLoginWizard()
        val result = createResult {
            wizard.loginStageCustom(
                authParams = mapOf(
                    TYPE_PARAM_KEY to LOGIN_PASSWORD_TYPE,
                    PASSWORD_PARAM_KEY to password
                ),
                identifierParams = mapOf(
                    USER_PARAM_KEY to loginStagesDataSource.userName,
                    TYPE_PARAM_KEY to LOGIN_PASSWORD_USER_ID_TYPE
                )
            )
        }
        return when (result) {
            is Response.Success -> {
                loginStagesDataSource.setPassword(password)
                loginStagesDataSource.stageCompleted(result.data)
                Response.Success(Unit)
            }
            is Response.Error -> result
        }
    }

    companion object {
        private const val PASSWORD_PARAM_KEY = "password"
        private const val MINIMUM_LENGTH_KEY = "minimum_length"
        private const val USER_PARAM_KEY = "user"
    }
}