package org.futo.circles.feature.notifications.test.task

import android.content.Context
import org.futo.circles.R
import org.futo.circles.feature.notifications.NotificationUtils
import org.futo.circles.model.NotificationTestStatus


class NotificationTestSend(
    private val context: Context,
    private val notificationUtils: NotificationUtils
) :
    BaseNotificationTest(R.string.settings_troubleshoot_test_notification_title), TestPushClicker {

    override fun perform() {
        notificationUtils.displayDiagnosticNotification()
        description =
            context.getString(R.string.settings_troubleshoot_test_notification_notice)
        status = NotificationTestStatus.IDLE
    }

    override fun onTestPushClicked() {
        description =
            context.getString(R.string.settings_troubleshoot_test_notification_notification_clicked)
        quickFix = null
        status = NotificationTestStatus.SUCCESS
        updateTestInfo()
    }
}