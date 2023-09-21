package org.futo.circles.notifications.test

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.test.task.BaseNotificationTest
import org.matrix.android.sdk.api.session.pushers.PusherState
import javax.inject.Inject

class NotificationTestTokenRegistration @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fcmHelper: FcmHelper
) : BaseNotificationTest(R.string.settings_troubleshoot_test_token_registration_title) {

    override fun perform() {
        val fcmToken = fcmHelper.getFcmToken() ?: run {
            status = TaskStatus.FAILED
            return
        }
        val session = MatrixSessionProvider.currentSession ?: run {
            status = TaskStatus.FAILED
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
            status = TaskStatus.FAILED
        } else {
            description =
                context.getString(R.string.settings_troubleshoot_test_token_registration_success)
            status = TaskStatus.SUCCESS
        }
    }
}
