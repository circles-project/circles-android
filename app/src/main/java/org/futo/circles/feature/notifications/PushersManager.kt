package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.BuildConfig
import org.futo.circles.R
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.pushers.HttpPusher
import java.util.*
import kotlin.math.abs


private const val DEFAULT_PUSHER_FILE_TAG = "mobile"

class PushersManager(
    private val context: Context
) {

    fun enqueueRegisterPusherWithFcmKey(pushKey: String): UUID {
        return enqueueRegisterPusher(pushKey, context.getString(R.string.pusher_http_url))
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
        BuildConfig.APPLICATION_ID,
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
        currentSession.pushersService().removeHttpPusher(pushKey, BuildConfig.APPLICATION_ID)
    }
}
