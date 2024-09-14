package org.futo.circles.core.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.core.extensions.coroutineScope
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.session.Session


object MatrixSessionListenerProvider {

    private var onInvalidToken: ((GlobalError) -> Unit)? = null

    val sessionListener: Session.Listener = object : Session.Listener {
        override fun onGlobalError(session: Session, globalError: GlobalError) {
            if (globalError !is GlobalError.InvalidToken) return

            session.coroutineScope.launch(Dispatchers.IO) {
                session.syncService().stopSync()
                MatrixInstanceProvider.matrix.authenticationService()
                    .removeSession(session.sessionId)
                onInvalidToken?.invoke(globalError)
                onInvalidToken = null
            }
        }
    }

    fun setOnInvalidTokenListener(callback: (GlobalError) -> Unit) {
        onInvalidToken = callback
    }

}