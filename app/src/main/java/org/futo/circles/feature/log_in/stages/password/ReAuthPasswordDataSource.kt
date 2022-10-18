package org.futo.circles.feature.log_in.stages.password

import org.futo.circles.core.LOGIN_PASSWORD_TYPE
import org.futo.circles.core.TYPE_PARAM_KEY
import org.futo.circles.core.auth.BaseLoginStagesDataSource.Companion.USER_PARAM_KEY
import org.futo.circles.core.auth.PasswordDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.feature.reauth.ReAuthStagesDataSource

class ReAuthPasswordDataSource(
    private val reAuthStagesDataSource: ReAuthStagesDataSource
) : PasswordDataSource {

    override fun getMinimumPasswordLength(): Int = 1

    override suspend fun processPasswordStage(password: String): Response<Unit> {
        val result = reAuthStagesDataSource.performLoginStage(
            mapOf(
                TYPE_PARAM_KEY to LOGIN_PASSWORD_TYPE,
                LoginPasswordDataSource.PASSWORD_PARAM_KEY to password,
                USER_PARAM_KEY to "@${reAuthStagesDataSource.userName}:${reAuthStagesDataSource.domain}"
            ), password
        )
        return when (result) {
            is Response.Success -> Response.Success(Unit)
            is Response.Error -> result
        }
    }
}