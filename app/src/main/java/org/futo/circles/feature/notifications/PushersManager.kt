package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.PUSHER_APP_ID
import org.futo.circles.core.utils.ConfigUtils.getPusherUrl
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.pushers.HttpPusher
import org.unifiedpush.android.connector.UnifiedPush
import java.util.*
import kotlin.math.abs


private const val DEFAULT_PUSHER_FILE_TAG = "mobile"

class PushersManager(private val context: Context, private val fcmHelper: FcmHelper) {

    suspend fun testPush() {
        MatrixSessionProvider.currentSession?.pushersService()?.testPush(
            getPusherUrl(),
            PUSHER_APP_ID,
            fcmHelper.getFcmToken().orEmpty(),
            TEST_EVENT_ID
        )
    }

    fun enqueueRegisterPusherWithFcmKey(pushKey: String): UUID {
        val currentSession = MatrixSessionProvider.currentSession
        val pusher = createHttpPusher(pushKey)
        return currentSession?.pushersService()?.enqueueAddHttpPusher(pusher) ?: UUID.randomUUID()
    }

    fun registerPushNotifications(context: Context) {
        UnifiedPush.saveDistributor(context, PUSHER_APP_ID)
        UnifiedPush.registerApp(context)
        if (fcmHelper.isFirebaseAvailable())
            fcmHelper.ensureFcmTokenIsRetrieved(this, shouldAddHttpPusher())
    }

    private fun shouldAddHttpPusher(): Boolean {
        val currentSession = MatrixSessionProvider.currentSession ?: return false
        val currentPushers = currentSession.pushersService().getPushers()
        return currentPushers.none { it.deviceId == currentSession.sessionParams.deviceId }
    }

    private fun createHttpPusher(
        pushKey: String
    ) = HttpPusher(
        pushKey,
        PUSHER_APP_ID,
        profileTag = DEFAULT_PUSHER_FILE_TAG + "_" + abs(MatrixSessionProvider.currentSession?.myUserId.hashCode()),
        Locale.getDefault().language,
        context.getString(R.string.app_name),
        MatrixSessionProvider.currentSession?.sessionParams?.deviceId ?: DEFAULT_PUSHER_FILE_TAG,
        getPusherUrl(),
        enabled = true,
        deviceId = MatrixSessionProvider.currentSession?.sessionParams?.deviceId
            ?: DEFAULT_PUSHER_FILE_TAG,
        append = false,
        withEventIdOnly = true
    )

    companion object {
        const val TEST_EVENT_ID = "\$THIS_IS_A_FAKE_EVENT_ID"
    }
}
