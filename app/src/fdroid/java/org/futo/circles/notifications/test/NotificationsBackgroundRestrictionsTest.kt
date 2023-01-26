package org.futo.circles.notifications.test

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import androidx.core.net.ConnectivityManagerCompat
import org.futo.circles.R
import org.futo.circles.feature.notifications.test.task.BaseNotificationTest
import org.futo.circles.model.NotificationTestStatus

class NotificationsBackgroundRestrictionsTest(
    private val context: Context
) : BaseNotificationTest(R.string.settings_troubleshoot_test_bg_restricted_title) {

    override fun perform() {
        context.getSystemService<ConnectivityManager>()!!.apply {
            // Checks if the device is on a metered network
            if (isActiveNetworkMetered) {
                // Checks user’s Data Saver settings.
                when (ConnectivityManagerCompat.getRestrictBackgroundStatus(this)) {
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED -> {
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.
                        description = context.getString(
                            R.string.settings_troubleshoot_test_bg_restricted_failed,
                            "RESTRICT_BACKGROUND_STATUS_ENABLED"
                        )
                        status = NotificationTestStatus.FAILED
                        quickFix = null
                    }
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED -> {
                        // The app is whitelisted. Wherever possible,
                        // the app should use less data in the foreground and background.
                        description = context.getString(
                            R.string.settings_troubleshoot_test_bg_restricted_success,
                            "RESTRICT_BACKGROUND_STATUS_WHITELISTED"
                        )
                        status = NotificationTestStatus.SUCCESS
                        quickFix = null
                    }
                    ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED -> {
                        // Data Saver is disabled. Since the device is connected to a
                        // metered network, the app should use less data wherever possible.
                        description = context.getString(
                            R.string.settings_troubleshoot_test_bg_restricted_success,
                            "RESTRICT_BACKGROUND_STATUS_DISABLED"
                        )
                        status = NotificationTestStatus.SUCCESS
                        quickFix = null
                    }
                }
            } else {
                // The device is not on a metered network.
                // Use data as required to perform syncs, downloads, and updates.
                description = context.getString(
                    R.string.settings_troubleshoot_test_bg_restricted_success,
                    ""
                )
                status = NotificationTestStatus.SUCCESS
                quickFix = null
            }
        }
    }
}