package org.futo.circles.feature.log_in.stages.password

import org.futo.circles.core.LOGIN_PASSWORD_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.log_in.stages.LoginStagesDataSource
import org.matrix.android.sdk.api.auth.registration.Stage

class LoginPasswordDataSource(
    private val loginStagesDataSource: LoginStagesDataSource
) : PasswordDataSource {

    override fun getMinimumPasswordLength(): Int =
        ((loginStagesDataSource.currentStage as? Stage.Other)?.params?.getOrDefault(
            MINIMUM_LENGTH_KEY, 1.0
        ) as? Double)?.toInt() ?: 1


    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val result = loginStagesDataSource.performLoginStage(
            mapOf(
                TYPE_PARAM_KEY to LOGIN_PASSWORD_TYPE,
                PASSWORD_PARAM_KEY to password
            ), password
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }

    companion object {
        private const val PASSWORD_PARAM_KEY = "password"
        private const val MINIMUM_LENGTH_KEY = "minimum_length"
    }
}