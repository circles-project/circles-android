package org.futo.circles.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.feature.notifications.PushersManager
import javax.inject.Inject

class NotificationsUnifiedPushGatewayTest @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pushersManager: PushersManager
) : BaseNotificationTest(R.string.settings_troubleshoot_test_current_gateway_title) {

    override fun perform() {
        description = context.getString(
            R.string.settings_troubleshoot_test_current_gateway,
            pushersManager.getPushGateway()
        )
        status = TaskStatus.SUCCESS
    }
}