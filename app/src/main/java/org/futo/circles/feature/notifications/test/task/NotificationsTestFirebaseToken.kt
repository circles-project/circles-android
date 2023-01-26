package org.futo.circles.feature.notifications.test.task

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import org.futo.circles.R
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.model.NotificationTestStatus

class NotificationsTestFirebaseToken(
    private val context: Context,
    private val fcmHelper: FcmHelper,
) : BaseNotificationTest(R.string.settings_troubleshoot_test_fcm_title) {

    override fun perform() {
        status = NotificationTestStatus.RUNNING
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    description = task.exception?.localizedMessage ?: "Unknown"
                    status = NotificationTestStatus.FAILED
                } else {
                    task.result?.let { token ->
                        val tok = token.take(8) + "********************"
                        description = context.getString(
                            R.string.settings_troubleshoot_test_fcm_success,
                            tok
                        )
                        fcmHelper.storeFcmToken(token)
                    }
                    status = NotificationTestStatus.SUCCESS
                }
            }
        } catch (e: Throwable) {
            description = context.getString(
                R.string.settings_troubleshoot_test_fcm_failed,
                e.localizedMessage
            )
            status = NotificationTestStatus.FAILED
        }
    }
}