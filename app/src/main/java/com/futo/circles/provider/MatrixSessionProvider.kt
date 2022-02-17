package com.futo.circles.provider

import android.content.Context
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.session.Session

class MatrixSessionProvider(private val context: Context) {

    var currentSession: Session? = null
        private set

    fun initSession() {
        Matrix.initialize(
            context = context, matrixConfiguration = MatrixConfiguration(
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl()
            )
        )
        val matrixInstance =
            Matrix.getInstance(context).also { MatrixProvider.saveMatrixInstance(it) }

        val lastSession =
            matrixInstance.authenticationService().getLastAuthenticatedSession()

        lastSession?.let { startSession(it) }
    }

    fun startSession(session: Session) {
        currentSession = session.apply { open(); startSync(true) }
    }
}