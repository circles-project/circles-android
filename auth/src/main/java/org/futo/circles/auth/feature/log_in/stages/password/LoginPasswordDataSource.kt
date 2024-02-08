package org.futo.circles.auth.feature.log_in.stages.password

import org.futo.circles.auth.base.BaseLoginStagesDataSource
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.LOGIN_PASSWORD_TYPE
import org.futo.circles.auth.base.BaseLoginStagesDataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.base.PasswordDataSource
import org.futo.circles.core.extensions.Response

class LoginPasswordDataSource(
    private val loginStagesDataSource: BaseLoginStagesDataSource
) : PasswordDataSource {

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
        const val PASSWORD_PARAM_KEY = "password"
    }
}