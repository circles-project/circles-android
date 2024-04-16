package org.futo.circles.core.notifications.test

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.R
import org.futo.circles.core.feature.notifications.FcmHelper
import org.futo.circles.core.feature.notifications.test.task.BaseNotificationTest
import org.futo.circles.core.model.TaskStatus
import javax.inject.Inject

class NotificationsTestFirebaseToken @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fcmHelper: FcmHelper
) : BaseNotificationTest(R.string.settings_troubleshoot_test_fcm_title) {

    override fun perform() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    description = task.exception?.localizedMessage ?: "Unknown"
                    status = TaskStatus.FAILED
                } else {
                    task.result?.let { token ->
                        val tok = token.take(8) + "********************"
                        description = context.getString(
                            R.string.settings_troubleshoot_test_fcm_success,
                            tok
                        )
                        fcmHelper.storeFcmToken(token)
                    }
                    status = TaskStatus.SUCCESS
                }
                updateTestInfo()
            }
        } catch (e: Throwable) {
            description = context.getString(
                R.string.settings_troubleshoot_test_fcm_failed,
                e.localizedMessage
            )
            status = TaskStatus.FAILED
            updateTestInfo()
        }
    }
}