package org.futo.circles.auth.feature.log_in.log_out

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class LogoutDataSource @Inject constructor() {

    suspend fun logOut() = createResult {
        MatrixSessionProvider.getSessionOrThrow().signOutService().signOut(true)
    }

}