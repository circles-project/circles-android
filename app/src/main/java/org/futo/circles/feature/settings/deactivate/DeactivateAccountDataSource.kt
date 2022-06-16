package org.futo.circles.feature.settings.deactivate

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider

class DeactivateAccountDataSource(
    context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    suspend fun deactivateAccount(password: String): Response<Unit> = createResult {
        session.accountService()
            .deactivateAccount(false, authConfirmationProvider.getAuthInterceptor(password))
    }
}