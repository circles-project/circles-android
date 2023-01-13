package org.futo.circles.feature.notifications

import android.app.Activity
import android.se.omapi.Session

interface FcmHelper {
    fun isFirebaseAvailable(): Boolean

    fun getFcmToken(): String?

    fun storeFcmToken(token: String?)

    fun ensureFcmTokenIsRetrieved(
        activity: Activity,
        pushersManager: PushersManager,
        registerPusher: Boolean
    )

    fun onEnterForeground(session: Session)

    fun onEnterBackground(session: Session)
}
