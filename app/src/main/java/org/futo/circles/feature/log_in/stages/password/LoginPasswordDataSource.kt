package org.futo.circles.feature.log_in.stages.password

import android.content.Context
import android.net.Uri
import org.futo.circles.R
import org.futo.circles.core.utils.HomeServerUtils
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.session.Session

class LoginPasswordDataSource(
    private val context: Context
) {

    suspend fun logIn(name: String, password: String): Response<Session> = createResult {
        val session = MatrixInstanceProvider.matrix.authenticationService().directAuthentication(
            homeServerConnectionConfig = buildHomeServerConfig(name),
            matrixId = name,
            password = password,
            initialDeviceName = context.getString(
                R.string.initial_device_name,
                context.getString(R.string.app_name)
            )
        )
        MatrixSessionProvider.awaitForSessionSync(session)
    }

    private fun buildHomeServerConfig(userName: String): HomeServerConnectionConfig {
        val url = HomeServerUtils.getHomeServerUrlFromUserName(userName)
        return HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(url))
            .build()
    }
}