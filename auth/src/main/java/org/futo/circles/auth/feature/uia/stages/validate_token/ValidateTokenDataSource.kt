package org.futo.circles.auth.feature.uia.stages.validate_token

import org.futo.circles.auth.feature.uia.UIADataSource.Companion.LOGIN_REGISTRATION_TOKEN_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class ValidateTokenDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun validateToken(token: String): Response<RegistrationResult> =
        uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to LOGIN_REGISTRATION_TOKEN_TYPE,
                TOKEN_PARAM_KEY to token
            )
        )

    companion object {
        private const val TOKEN_PARAM_KEY = "token"
    }
}