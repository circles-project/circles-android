package org.futo.circles.feature.notifications.test.task

import android.content.Context
import org.futo.circles.R
import org.futo.circles.feature.notifications.UnifiedPushHelper
import org.futo.circles.model.NotificationTestStatus
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.pushers.PusherState


class NotificationsEndpointAsTokenRegistrationTest(
    private val context: Context,
    private val unifiedPushHelper: UnifiedPushHelper
) : BaseNotificationTest(R.string.settings_troubleshoot_test_endpoint_registration_title) {

    override fun perform() {
        // Check if we have a registered pusher for this token
        val endpoint = unifiedPushHelper.getEndpointOrToken() ?: run {
            status = NotificationTestStatus.FAILED
            return
        }
        val session = MatrixSessionProvider.currentSession ?: run {
            status = NotificationTestStatus.FAILED
            return
        }
        val pushers = session.pushersService().getPushers().filter {
            it.pushKey == endpoint && it.state == PusherState.REGISTERED
        }
        if (pushers.isEmpty()) {
            description = context.getString(
                R.string.settings_troubleshoot_test_endpoint_registration_failed,
                context.getString(R.string.unexpected_error)
            )
            status = NotificationTestStatus.FAILED
        } else {
            description =
                context.getString(R.string.settings_troubleshoot_test_endpoint_registration_success)
            status = NotificationTestStatus.SUCCESS
        }
    }
}
