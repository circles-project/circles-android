package org.futo.circles.feature.notifications.test.task

import android.content.Context
import org.futo.circles.R
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.model.NotificationTestStatus


class NotificationCurrentPushDistributorTest(
    private val context: Context,
    private val fcmHelper: FcmHelper,
) : BaseNotificationTest(R.string.settings_troubleshoot_test_current_distributor_title) {

    override fun perform() {
        description = context.getString(
            R.string.settings_troubleshoot_test_current_distributor,
            getCurrentDistributorName()
        )
        status = NotificationTestStatus.SUCCESS
    }

    private fun getCurrentDistributorName(): String =
        if (fcmHelper.isFirebaseAvailable()) context.getString(R.string.unifiedpush_distributor_fcm_fallback)
        else context.getString(R.string.unifiedpush_distributor_background_sync)


}