package com.futo.circles.feature.settings.deactivate

import android.content.Context
import com.futo.circles.R
import com.futo.circles.core.matrix.auth.AuthConfirmationProvider
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider

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