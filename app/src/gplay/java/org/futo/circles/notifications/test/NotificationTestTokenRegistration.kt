package org.futo.circles.notifications.test

import android.content.Context
import org.futo.circles.R
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.test.task.BaseNotificationTest
import org.futo.circles.model.NotificationTestStatus
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.pushers.PusherState

class NotificationTestTokenRegistration(
    private val context: Context,
    private val fcmHelper: FcmHelper
) : BaseNotificationTest(R.string.settings_troubleshoot_test_token_registration_title) {

    override fun perform() {
        val fcmToken = fcmHelper.getFcmToken() ?: run {
            status = NotificationTestStatus.FAILED
            return
        }
        val session = MatrixSessionProvider.currentSession ?: run {
            status = NotificationTestStatus.FAILED
            return
        }
        val pushers = session.pushersService().getPushers().filter {
            it.pushKey == fcmToken && it.state == PusherState.REGISTERED
        }
        if (pushers.isEmpty()) {
            description = context.getString(
                R.string.settings_troubleshoot_test_token_registration_failed,
                context.getString(R.string.unexpected_error)
            )
            status = NotificationTestStatus.FAILED
        } else {
            description =
                context.getString(R.string.settings_troubleshoot_test_token_registration_success)
            status = NotificationTestStatus.SUCCESS
        }
    }
}
