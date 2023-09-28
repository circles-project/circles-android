package org.futo.circles.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.feature.notifications.NotificationUtils
import javax.inject.Inject


class NotificationTestSend @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationUtils: NotificationUtils
) : BaseNotificationTest(R.string.settings_troubleshoot_test_notification_title), TestPushClicker {

    override fun perform() {
        notificationUtils.displayDiagnosticNotification()
        description =
            context.getString(R.string.settings_troubleshoot_test_notification_notice)
        status = TaskStatus.IDLE
    }

    override fun onTestPushClicked() {
        description =
            context.getString(R.string.settings_troubleshoot_test_notification_notification_clicked)
        quickFix = null
        status = TaskStatus.SUCCESS
        updateTestInfo()
    }
}