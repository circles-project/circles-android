package org.futo.circles.feature.settings.active_sessions.remove_session

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.util.awaitCallback


class RemoveSessionDataSource(
    private val deviceId: String,
    context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    suspend fun removeSession(password: String): Response<Unit> = createResult {
        awaitCallback<Unit> {
            session.cryptoService()
                .deleteDevice(deviceId, authConfirmationProvider.getAuthInterceptor(password), it)
        }
    }
}