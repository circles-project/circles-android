package org.futo.circles.core.notifications.test

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.R
import org.futo.circles.core.feature.notifications.test.task.BaseNotificationTest
import org.futo.circles.core.model.TaskStatus
import javax.inject.Inject

class NotificationsTestPlayServices @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseNotificationTest(R.string.settings_troubleshoot_test_play_services_title) {

    override fun perform() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
        if (resultCode == ConnectionResult.SUCCESS) {
            quickFix = null
            description =
                context.getString(R.string.settings_troubleshoot_test_play_services_success)
            status = TaskStatus.SUCCESS
        } else {
            description = context.getString(
                R.string.settings_troubleshoot_test_play_services_failed,
                apiAvailability.getErrorString(resultCode)
            )
            status = TaskStatus.FAILED
        }
    }
}
