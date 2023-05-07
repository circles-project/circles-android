package org.futo.circles.provider

import android.util.Log
import org.matrix.android.sdk.api.failure.GlobalError
import org.matrix.android.sdk.api.session.Session


object MatrixSessionListenerProvider {

    private var onInvalidToken: ((GlobalError) -> Unit)? = null

    val sessionListener: Session.Listener = object : Session.Listener {
        override fun onGlobalError(session: Session, globalError: GlobalError) {
            Log.d("MyLog", globalError.toString())
            if (globalError is GlobalError.InvalidToken) {
                onInvalidToken?.invoke(globalError)
                onInvalidToken = null
            }

        }
    }

    fun setOnInvalidTokenListener(callback: (GlobalError) -> Unit) {
        onInvalidToken = callback
    }

}