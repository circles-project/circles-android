package com.futo.circles.ui.log_in.data_source

import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixHomeServerProvider
import com.futo.circles.provider.MatrixProvider
import com.futo.circles.provider.MatrixSessionProvider
import java.util.*

class LoginDataSource(private val matrixSessionProvider: MatrixSessionProvider) {

    suspend fun logIn(name: String, password: String, secondPassword: String?) =
        createResult {
            val homeServerConnectionConfig = MatrixHomeServerProvider().createHomeServerConfig()

            MatrixProvider.matrix.authenticationService().directAuthentication(
                homeServerConnectionConfig = homeServerConnectionConfig,
                matrixId = name,
                password = password,
                deviceId = secondPassword,
                initialDeviceName = UUID.randomUUID().toString()
            ).also { matrixSessionProvider.startSession(it) }
        }
}