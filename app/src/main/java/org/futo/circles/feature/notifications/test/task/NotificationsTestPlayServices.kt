package org.futo.circles.feature.notifications.test.task

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import org.futo.circles.R
import org.futo.circles.model.NotificationTestStatus

class NotificationsTestPlayServices(
    private val context: Context
) : BaseNotificationTest(R.string.settings_troubleshoot_test_play_services_title) {

    override fun perform() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode == ConnectionResult.SUCCESS) {
            quickFix = null
            description =
                context.getString(R.string.settings_troubleshoot_test_play_services_success)
            status = NotificationTestStatus.SUCCESS
        } else {
            description = context.getString(
                R.string.settings_troubleshoot_test_play_services_failed,
                apiAvailability.getErrorString(resultCode)
            )
            status = NotificationTestStatus.FAILED
        }
    }
}
