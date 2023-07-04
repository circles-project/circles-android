package org.futo.circles.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.model.NotificationTestStatus
import org.matrix.android.sdk.api.session.pushers.PusherState
import javax.inject.Inject

class NotificationsEndpointAsTokenRegistrationTest @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pushersManager: PushersManager
) : BaseNotificationTest(R.string.settings_troubleshoot_test_endpoint_registration_title) {

    override fun perform() {
        val endpoint = pushersManager.getEndpointOrToken() ?: run {
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
