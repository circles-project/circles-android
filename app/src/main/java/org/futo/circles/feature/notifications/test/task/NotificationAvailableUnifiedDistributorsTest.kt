package org.futo.circles.feature.notifications.test.task

import android.content.Context
import org.futo.circles.R
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.model.NotificationTestStatus
import org.unifiedpush.android.connector.UnifiedPush


class NotificationAvailableUnifiedDistributorsTest(
    private val context: Context,
    private val fcmHelper: FcmHelper,
) : BaseNotificationTest(R.string.settings_troubleshoot_test_distributors_title) {

    override fun perform() {
        val distributors = getExternalDistributors()
        description = if (distributors.isEmpty()) {
            context.getString(
                if (fcmHelper.isFirebaseAvailable()) {
                    R.string.settings_troubleshoot_test_distributors_gplay
                } else {
                    R.string.settings_troubleshoot_test_distributors_fdroid
                }
            )
        } else {
            val quantity = distributors.size + 1
            context.resources.getQuantityString(
                R.plurals.settings_troubleshoot_test_distributors_many,
                quantity,
                quantity
            )
        }
        status = NotificationTestStatus.SUCCESS
    }

    private fun getExternalDistributors(): List<String> {
        return UnifiedPush.getDistributors(context).filterNot { it == context.packageName }
    }
}