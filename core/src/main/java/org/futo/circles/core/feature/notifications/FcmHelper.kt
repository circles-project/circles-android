package org.futo.circles.core.feature.notifications

interface FcmHelper {
    fun isFirebaseAvailable(): Boolean

    fun getFcmToken(): String?

    fun storeFcmToken(token: String?)

    fun ensureFcmTokenIsRetrieved(pushersManager: PushersManager, registerPusher: Boolean)

    fun onEnterForeground()

    fun onEnterBackground()
}
