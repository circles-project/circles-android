package org.futo.circles.auth.feature.token

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import javax.inject.Inject

class RefreshTokenDataSource @Inject constructor() {

    // Local sessionId from Session.Params.Credentials.sessionId()
    suspend fun refreshToken(sessionId: String) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().refreshToken(sessionId)
    }

}