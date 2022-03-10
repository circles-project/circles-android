package com.futo.circles.feature.log_in.data_source

import android.content.Context
import android.net.Uri
import com.futo.circles.BuildConfig
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixInstanceProvider
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

class LoginDataSource(private val context: Context) {

    suspend fun logIn(name: String, password: String) =
        createResult {
            MatrixInstanceProvider.matrix.authenticationService().directAuthentication(
                homeServerConnectionConfig = createHomeServerConfig(),
                matrixId = name,
                password = password,
                initialDeviceName = context.getString(
                    R.string.initial_device_name,
                    context.getString(R.string.app_name)
                )
            ).also { MatrixSessionProvider.startSession(it) }
        }

    private fun createHomeServerConfig() =
        HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(BuildConfig.MATRIX_HOME_SERVER_URL))
            .build()
}