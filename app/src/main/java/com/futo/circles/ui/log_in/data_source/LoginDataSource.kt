package com.futo.circles.ui.log_in.data_source

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixHomeServerProvider
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.Matrix

class LoginDataSource(
    private val context: Context,
    private val matrixSessionProvider: MatrixSessionProvider
) {

    suspend fun logIn(name: String, password: String, secondPassword: String?) =
        createResult {
            val homeServerConnectionConfig = MatrixHomeServerProvider().createHomeServerConfig()

            Matrix.getInstance(context).authenticationService().directAuthentication(
                homeServerConnectionConfig = homeServerConnectionConfig,
                matrixId = name,
                password = password,
                deviceId = secondPassword,
                initialDeviceName = context.getString(
                    R.string.initial_device_name,
                    context.getString(R.string.app_name)
                )
            ).also { matrixSessionProvider.startSession(it) }
        }
}