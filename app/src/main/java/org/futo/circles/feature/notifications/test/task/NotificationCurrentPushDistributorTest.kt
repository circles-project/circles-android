package org.futo.circles.feature.notifications.test.task

import android.content.Context
import org.futo.circles.R
import org.futo.circles.feature.notifications.UnifiedPushHelper
import org.futo.circles.model.NotificationTestStatus


class NotificationCurrentPushDistributorTest(
    private val context: Context,
    private val unifiedPushHelper: UnifiedPushHelper,
) : BaseNotificationTest(R.string.settings_troubleshoot_test_current_distributor_title) {

    override fun perform() {
        description = context.getString(
            R.string.settings_troubleshoot_test_current_distributor,
            unifiedPushHelper.getCurrentDistributorName()
        )
        status = NotificationTestStatus.SUCCESS
    }
}