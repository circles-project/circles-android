package org.futo.circles.provider

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
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

        val lastSession = try {
            MatrixInstanceProvider.matrix.authenticationService().getLastAuthenticatedSession()
        } catch (e: Exception) {
            null
        }

        lastSession?.let { startSession(it) }
    }

    private fun startSession(session: Session, listener: Session.Listener? = null) {
        listener?.let { session.addListener(it) }
        currentSession = session.apply { open(); startSync(true) }
    }

    suspend fun awaitForSessionStart(session: Session) =
        suspendCancellableCoroutine<Session> {
            startSession(session, object : Session.Listener {
                override fun onSessionStarted(session: Session) {
                    super.onSessionStarted(session)
                    it.resume(session) { session.removeListener(this) }
                }
            })
        }
}