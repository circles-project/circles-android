package org.futo.circles.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.model.NotificationTestStatus
import javax.inject.Inject


class NotificationCurrentPushDistributorTest @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pushersManager: PushersManager
) : BaseNotificationTest(R.string.settings_troubleshoot_test_current_distributor_title) {

    override fun perform() {
        description = context.getString(
            R.string.settings_troubleshoot_test_current_distributor,
            pushersManager.getCurrentDistributorName()
        )
        status = NotificationTestStatus.SUCCESS
    }

}