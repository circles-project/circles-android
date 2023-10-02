package org.futo.circles.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.PushersManager
import javax.inject.Inject


class NotificationAvailableUnifiedDistributorsTest @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fcmHelper: FcmHelper,
    private val pushersManager: PushersManager
) : BaseNotificationTest(R.string.settings_troubleshoot_test_distributors_title) {

    override fun perform() {
        val distributors = pushersManager.getExternalDistributors()
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
        status = TaskStatus.SUCCESS
    }
}