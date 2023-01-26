package org.futo.circles.notifications.test

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import org.futo.circles.R
import org.futo.circles.feature.notifications.test.task.BaseNotificationTest
import org.futo.circles.model.NotificationTestStatus

class NotificationsBatteryOptimizationTest(
    private val context: Context
) : BaseNotificationTest(R.string.settings_troubleshoot_test_battery_title) {

    override fun perform() {
        if (isIgnoringBatteryOptimizations()) {
            description = context.getString(R.string.settings_troubleshoot_test_battery_success)
            status = NotificationTestStatus.SUCCESS
            quickFix = null
        } else {
            description = context.getString(R.string.settings_troubleshoot_test_battery_failed)
            status = NotificationTestStatus.FAILED
        }
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        return context.getSystemService<PowerManager>()
            ?.isIgnoringBatteryOptimizations(context.packageName) == true
    }
}