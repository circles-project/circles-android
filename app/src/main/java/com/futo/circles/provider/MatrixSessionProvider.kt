package com.futo.circles.provider

import android.content.Context
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.session.Session

object MatrixSessionProvider {

    var currentSession: Session? = null
        private set

    fun initSession(context: Context) {
        Matrix.createInstance(
            context = context, matrixConfiguration = MatrixConfiguration(
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl()
            )
        ).also { MatrixInstanceProvider.saveMatrixInstance(it) }

        val lastSession =
            MatrixInstanceProvider.matrix.authenticationService().getLastAuthenticatedSession()

        lastSession?.let { startSession(it) }
    }

    fun startSession(session: Session, listener: Session.Listener? = null) {
        listener?.let { session.addListener(it) }
        currentSession = session.apply { open(); startSync(true) }
    }
}