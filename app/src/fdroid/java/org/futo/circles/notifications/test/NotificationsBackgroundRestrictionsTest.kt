package org.futo.circles.notifications.test

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.core.net.ConnectivityManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.feature.notifications.test.task.BaseNotificationTest
import javax.inject.Inject

class NotificationsBackgroundRestrictionsTest @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseNotificationTest(R.string.settings_troubleshoot_test_bg_restricted_title) {

    override fun perform() {
        context.getSystemService<ConnectivityManager>()!!.apply {
            if (isActiveNetworkMetered) {
                when (ConnectivityManagerCompat.getRestrictBackgroundStatus(this)) {
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED -> {
                        description = context.getString(
                            R.string.settings_troubleshoot_test_bg_restricted_failed,
                            "RESTRICT_BACKGROUND_STATUS_ENABLED"
                        )
                        status = TaskStatus.FAILED
                        quickFix = null
                    }

                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED -> {
                        description = context.getString(
                            R.string.settings_troubleshoot_test_bg_restricted_success,
                            "RESTRICT_BACKGROUND_STATUS_WHITELISTED"
                        )
                        status = TaskStatus.SUCCESS
                        quickFix = null
                    }

                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED -> {
                        description = context.getString(
                            R.string.settings_troubleshoot_test_bg_restricted_success,
                            "RESTRICT_BACKGROUND_STATUS_DISABLED"
                        )
                        status = TaskStatus.SUCCESS
                        quickFix = null
                    }
                }
            } else {
                description = context.getString(
                    R.string.settings_troubleshoot_test_bg_restricted_success,
                    ""
                )
                status = TaskStatus.SUCCESS
                quickFix = null
            }
        }
    }
}