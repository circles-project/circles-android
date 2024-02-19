package org.futo.circles.core.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.coroutineScope
import org.futo.circles.core.extensions.createResult
import org.matrix.android.sdk.api.auth.data.sessionId
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.session.Session


object MatrixSessionListenerProvider {

    private var onInvalidToken: ((GlobalError) -> Unit)? = null
    var isRunning = false

    val sessionListener: Session.Listener = object : Session.Listener {
        override fun onGlobalError(session: Session, globalError: GlobalError) {
            if (globalError !is GlobalError.InvalidToken) return
            if (isRunning) return
            isRunning = true

            session.coroutineScope.launch(Dispatchers.IO) {
                val refreshResult = createResult {
                    MatrixInstanceProvider.matrix.authenticationService()
                        .refreshToken(session.sessionParams.credentials.sessionId())
                }

                if (refreshResult is Response.Error) {
                    withContext(Dispatchers.Main) {
                        session.syncService().stopSync()
                        MatrixInstanceProvider.matrix.authenticationService()
                            .removeSession(session.sessionParams.credentials.sessionId())
                        onInvalidToken?.invoke(globalError)
                        onInvalidToken = null
                    }
                }
                isRunning = false
            }
        }
    }

    fun setOnInvalidTokenListener(callback: (GlobalError) -> Unit) {
        onInvalidToken = callback
    }

}