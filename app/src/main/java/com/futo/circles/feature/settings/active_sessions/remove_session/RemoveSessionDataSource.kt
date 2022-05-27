package com.futo.circles.feature.settings.active_sessions.remove_session

import android.content.Context
import com.futo.circles.R
import com.futo.circles.core.matrix.auth.AuthConfirmationProvider
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider
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