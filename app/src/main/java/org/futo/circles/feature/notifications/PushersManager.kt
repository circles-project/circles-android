package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.R
import org.futo.circles.core.PUSHER_APP_ID
import org.futo.circles.core.PUSHER_URL
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.pushers.HttpPusher
import java.util.*
import kotlin.math.abs


private const val DEFAULT_PUSHER_FILE_TAG = "mobile"

class PushersManager(
    private val context: Context,
    private val unifiedPushHelper: UnifiedPushHelper
) {

    suspend fun testPush() {
        val currentSession = MatrixSessionProvider.currentSession ?: return

        currentSession.pushersService().testPush(
            unifiedPushHelper.getPushGateway() ?: return,
            PUSHER_APP_ID,
            unifiedPushHelper.getEndpointOrToken().orEmpty(),
            TEST_EVENT_ID
        )
    }

    fun enqueueRegisterPusherWithFcmKey(pushKey: String): UUID {
        return enqueueRegisterPusher(pushKey, PUSHER_URL)
    }

    fun enqueueRegisterPusher(
        pushKey: String,
        gateway: String
    ): UUID {
        val currentSession = MatrixSessionProvider.currentSession
        val pusher = createHttpPusher(pushKey, gateway)
        return currentSession?.pushersService()?.enqueueAddHttpPusher(pusher) ?: UUID.randomUUID()
    }

    private fun createHttpPusher(
        pushKey: String,
        gateway: String
    ) = HttpPusher(
        pushKey,
        PUSHER_APP_ID,
        profileTag = DEFAULT_PUSHER_FILE_TAG + "_" + abs(MatrixSessionProvider.currentSession?.myUserId.hashCode()),
        Locale.getDefault().language,
        context.getString(R.string.app_name),
        MatrixSessionProvider.currentSession?.sessionParams?.deviceId ?: DEFAULT_PUSHER_FILE_TAG,
        gateway,
        enabled = true,
        deviceId = MatrixSessionProvider.currentSession?.sessionParams?.deviceId
            ?: DEFAULT_PUSHER_FILE_TAG,
        append = false,
        withEventIdOnly = true
    )

    suspend fun unregisterPusher(pushKey: String) {
        val currentSession = MatrixSessionProvider.currentSession ?: return
        currentSession.pushersService().removeHttpPusher(pushKey, PUSHER_APP_ID)
    }

    companion object {
        const val TEST_EVENT_ID = "\$THIS_IS_A_FAKE_EVENT_ID"
    }
}
